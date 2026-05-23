// dto/SupportAgentDTO.java
package com.trackit.admin.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportAgentDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String status; // ACTIVE, INACTIVE
}