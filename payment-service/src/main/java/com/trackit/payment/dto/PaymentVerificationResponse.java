package com.trackit.payment.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationResponse {
    private boolean success;
    private String message;
    private String paymentId;
    private String orderId;
    private String razorpayPaymentId;
    private Double amount;
    private String paymentStatus;
    private LocalDateTime paymentTime;
    private String orderStatus;
    private LocalDate estimatedDelivery;
}