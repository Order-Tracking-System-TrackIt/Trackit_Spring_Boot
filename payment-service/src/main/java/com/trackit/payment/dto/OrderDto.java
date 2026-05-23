package com.trackit.payment.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String orderId;
    private String email;
    private String phonenumber;
    private String status;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime scanTime;
    private LocalDate estimatedDeliveryDate;
    private Double weight;
    private Double totalAmount;
}