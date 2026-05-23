package com.trackit.admin.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPerformanceDTO {
    private String month;
    private Double onTime;
    private Double delayed;
    private Integer totalOrders;
}