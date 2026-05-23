package com.trackit.payment.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.trackit.payment.client.OrderServiceClient;
import com.trackit.payment.dto.OrderDto;
import com.trackit.payment.dto.PaymentOrderRequest;
import com.trackit.payment.dto.PaymentOrderResponse;
import com.trackit.payment.dto.PaymentVerificationRequest;
import com.trackit.payment.dto.PaymentVerificationResponse;
import com.trackit.payment.entity.Payment;
import com.trackit.payment.repository.PaymentRepository;
import com.trackit.payment.service.EmailService;
import com.trackit.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;
    private final EmailService emailService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public PaymentOrderResponse createPaymentOrder(PaymentOrderRequest request, String userEmail)
            throws RazorpayException {

        log.info("🔄 Creating payment order for Order ID: {}", request.getOrderId());

        OrderDto orderDetails;
        try {
            orderDetails = orderServiceClient.getOrderByOrderId(request.getOrderId());
            log.info("✅ Order fetched - ID: {}, Amount: ₹{}", 
                orderDetails.getOrderId(), orderDetails.getTotalAmount());
        } catch (Exception e) {
            log.error("❌ Failed to fetch order: {}", e.getMessage());
            throw new RuntimeException("Order not found: " + request.getOrderId());
        }
        
        


        paymentRepository.findByOrderId(request.getOrderId())
                .ifPresent(p -> {
                    if ("COMPLETED".equalsIgnoreCase(p.getPaymentStatus())) {
                        throw new RuntimeException("Payment already completed");
                    }
                    if ("PENDING".equalsIgnoreCase(p.getPaymentStatus())) {
                        paymentRepository.delete(p);
                        log.info("🗑️ Deleted old pending payment");
                    }
                });


        Double baseAmount = orderDetails.getTotalAmount();
        if (baseAmount == null || baseAmount <= 0) {
            baseAmount = request.getAmount();
        }
        if (baseAmount == null || baseAmount <= 0) {
            throw new RuntimeException("Invalid amount. Amount must be greater than 0");
        }


        BigDecimal subtotal = BigDecimal.valueOf(baseAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = subtotal.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        log.info("💰 Price Breakdown:");
        log.info("   Subtotal: ₹{}", subtotal);
        log.info("   GST 18%:  ₹{}", taxAmount);
        log.info("   Total:    ₹{}", totalAmount);


        int amountInPaise = totalAmount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        if (amountInPaise < 100) {
            throw new RuntimeException("Amount too small. Minimum ₹1.00 required");
        }

     
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "rcpt_" + request.getOrderId());
        orderRequest.put("payment_capture", 1);

        JSONObject notes = new JSONObject();
        notes.put("order_id", request.getOrderId());
        notes.put("email", orderDetails.getEmail() != null ? orderDetails.getEmail() : "");
        notes.put("phone", orderDetails.getPhonenumber() != null ? orderDetails.getPhonenumber() : "");
        orderRequest.put("notes", notes);

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        String razorpayOrderId = razorpayOrder.get("id");

        log.info("✅ Razorpay Order created: {}", razorpayOrderId);

    
        Payment payment = Payment.builder()
                .paymentId("PAY-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .orderId(orderDetails.getOrderId())
                .razorpayOrderId(razorpayOrderId)
                .email(orderDetails.getEmail())
                .phonenumber(orderDetails.getPhonenumber())
                .amount(totalAmount.doubleValue())  // ✅ Store total (including tax)
                .paymentStatus("PENDING")
                .paymentMethod("RAZORPAY")
                .createdAt(LocalDateTime.now())
                .orderLocation(orderDetails.getLocation())
                .estimatedDelivery(orderDetails.getEstimatedDeliveryDate())
                .build();

        paymentRepository.save(payment);

        return PaymentOrderResponse.builder()
                .paymentId(payment.getPaymentId())
                .razorpayOrderId(razorpayOrderId)
                .amount(totalAmount.doubleValue()) 
                .currency("INR")
                .razorpayKeyId(razorpayKeyId)
                .orderId(orderDetails.getOrderId())
                .email(orderDetails.getEmail())
                .phonenumber(orderDetails.getPhonenumber())
                .orderLocation(orderDetails.getLocation())
                .build();
    }


    @Override
    public PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request) {

    	log.info("🔍 Verifying payment for Razorpay Order: {}", request.getRazorpayOrderId());

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValidSignature = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (!isValidSignature) {
                log.error("❌ Invalid payment signature");
                return PaymentVerificationResponse.builder()
                        .success(false)
                        .message("Payment verification failed: Invalid signature")
                        .build();
            }

            log.info("✅ Payment signature verified");

            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setPaymentStatus("COMPLETED");
            payment.setPaymentTime(LocalDateTime.now());

            Payment updatedPayment = paymentRepository.save(payment);

            log.info("💰 Payment completed - Payment ID: {}", updatedPayment.getPaymentId());

            log.info("📧 Triggering email notification...");
            emailService.sendPaymentConfirmationEmail(updatedPayment);

            return PaymentVerificationResponse.builder()
                    .success(true)
                    .message("Payment verified successfully")
                    .paymentId(updatedPayment.getPaymentId())
                    .orderId(updatedPayment.getOrderId())
                    .razorpayPaymentId(updatedPayment.getRazorpayPaymentId())
                    .amount(updatedPayment.getAmount())
                    .paymentStatus(updatedPayment.getPaymentStatus())
                    .paymentTime(updatedPayment.getPaymentTime())
                    .build();

        } catch (RazorpayException e) {
            log.error("❌ Razorpay verification failed: {}", e.getMessage());
            return PaymentVerificationResponse.builder()
                    .success(false)
                    .message("Payment verification failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }
    
    

    @Override
    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }
}