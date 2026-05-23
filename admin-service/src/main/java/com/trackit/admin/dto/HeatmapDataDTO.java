// dto/HeatmapDataDTO.java
package com.trackit.admin.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapDataDTO {
    private String orderId;
    private String location;
    private Double latitude;
    private Double longitude;
    private String status;
    private Integer orderCount; // For aggregated heatmap points
}