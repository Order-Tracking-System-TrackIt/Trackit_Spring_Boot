package com.trackit.payment.controller;

import com.razorpay.RazorpayException;
import com.trackit.payment.dto.*;
import com.trackit.payment.entity.Payment;
import com.trackit.payment.service.PaymentService;
import com.trackit.payment.service.EmailService;  // ✅ Add this
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final EmailService emailService; 
    
    @PostMapping("/create-order")
    public ResponseEntity<?> createPaymentOrder(@RequestBody PaymentOrderRequest request) {
        
        try {
            String userEmail = "customer@trackit.com";
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                userEmail = authentication.getName();
            }

            log.info("Payment order request - Order: {}, Amount: ₹{}, User: {}", 
                request.getOrderId(), request.getAmount(), userEmail);

            PaymentOrderResponse response = paymentService.createPaymentOrder(request, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RazorpayException e) {
            log.error("❌ Razorpay error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Razorpay error: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("❌ Payment error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create payment order");
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @RequestBody PaymentVerificationRequest request) {

        log.info("🔍 Payment verification request for: {}", request.getRazorpayOrderId());

        PaymentVerificationResponse response = paymentService.verifyPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(@PathVariable String orderId) {
        try {
            Payment payment = paymentService.getPaymentByOrderId(orderId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Payment not found for order: " + orderId);
        }
    }

    @GetMapping("/razorpay-key")
    public ResponseEntity<String> getRazorpayKeyId() {
        return ResponseEntity.ok(paymentService.getRazorpayKeyId());
    }

    @GetMapping("/test-email/{email}")
    public ResponseEntity<?> testEmail(@PathVariable String email) {
        try {
            log.info("🧪 Testing email to: {}", email);
            
            Payment testPayment = Payment.builder()
                    .paymentId("TEST-" + System.currentTimeMillis())
                    .orderId("TEST-ORDER-001")
                    .email(email)
                    .amount(118.0)
                    .paymentMethod("RAZORPAY")
                    .paymentStatus("COMPLETED")
                    .razorpayPaymentId("pay_test_123")
                    .paymentTime(LocalDateTime.now())
                    .build();
            
            emailService.sendPaymentConfirmationEmail(testPayment);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Test email sent! Check inbox and spam folder.",
                "email", email
            ));
        } catch (Exception e) {
            log.error("❌ Test email failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}