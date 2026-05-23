package com.trackit.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data              // Generates Getters (getScanTime), Setters, toString, etc.
@Builder           // Generates the .builder() method
@NoArgsConstructor // Required for Jackson (JSON parsing)
@AllArgsConstructor // Required for the @Builder to work
public class TrackingEventDto {

    private String id;
    private String orderId;
    private String email;
    
    // This field was likely missing or named differently
    private String phonenumber; 
    
    private String status;
    private String location;
    private Double latitude;
    private Double longitude;
    
    // This field was missing, causing "getScanTime() is undefined"
    private LocalDateTime scanTime; 
    
    // This field was likely missing
    private LocalDate estimatedDeliveryDate;
}