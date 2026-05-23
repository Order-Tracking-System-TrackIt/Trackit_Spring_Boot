// dto/RegionalDistributionDTO.java
package com.trackit.admin.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionalDistributionDTO {
    private String region;
    private Integer orders;
    private Double percentage;
    private Integer countries;
}