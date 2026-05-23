package com.trackit.payment.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private String orderId;
    private String email;
    private String phonenumber;
    private Double amount;
    private String paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentTime;
    
    private String orderStatus;
    private String orderLocation;
    private LocalDate estimatedDelivery;
}