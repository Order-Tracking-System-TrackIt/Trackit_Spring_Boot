// dto/KPIMetricsDTO.java
package com.trackit.admin.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KPIMetricsDTO {
    private Double onTimeRate;
    private String onTimeRateChange;
    private Double avgDeliveryTime;
    private String avgDeliveryTimeChange;
    private Integer activeShipments;      // Orders with status IN_TRANSIT or OUT_FOR_DELIVERY
    private String activeShipmentsChange;
    private Integer exceptions;
    private String exceptionsChange;
    private Double last30DaysRevenue;
    private String revenueChange;
    private Integer totalOrders;
    private Integer deliveredOrders;
}