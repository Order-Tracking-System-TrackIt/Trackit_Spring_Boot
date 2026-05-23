
package com.trackit.admin.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueDTO {
    private LocalDate date;
    private Double dailyRevenue;
    private Integer orderCount;
    private String period; // DAILY, WEEKLY, MONTHLY
}