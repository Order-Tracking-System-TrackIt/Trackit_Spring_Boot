
package com.trackit.admin.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private KPIMetricsDTO kpiMetrics;
    private List<OrderStatusDTO> orderStatusDistribution;
    private List<DeliveryPerformanceDTO> deliveryPerformance;
    private List<HeatmapDataDTO> heatmapData;
    private List<SupportAgentDTO> supportAgents;
    private List<RevenueDTO> revenueData;
}