package com.trackit.payment.service;

import com.razorpay.RazorpayException;
import com.trackit.payment.dto.*;
import com.trackit.payment.entity.Payment;

public interface PaymentService {

    PaymentOrderResponse createPaymentOrder(PaymentOrderRequest request, String userEmail)
            throws RazorpayException;

    PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request);

    Payment getPaymentByOrderId(String orderId);

    String getRazorpayKeyId();
}