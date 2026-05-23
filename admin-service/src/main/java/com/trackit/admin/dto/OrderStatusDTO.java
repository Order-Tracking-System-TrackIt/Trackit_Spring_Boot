// dto/OrderStatusDTO.java
package com.trackit.admin.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusDTO {
    private String name;
    private Integer value;
    private String color;
}