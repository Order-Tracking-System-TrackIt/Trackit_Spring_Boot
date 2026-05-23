package com.trackit.payment.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {
    private String paymentId;
    private String razorpayOrderId;
    private Double amount;
    private String currency;
    private String razorpayKeyId;
    private String orderId;
    private String email;
    private String phonenumber;
    private String orderLocation;
}