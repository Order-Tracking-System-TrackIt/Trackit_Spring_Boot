package com.trackit.tracking.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate; // <--- FIX: Correct package is java.time

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "tracking_events_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEventDocument {

    @Id
    private String id;
    private String orderId; 
    
    private String email;

    // NOTE: Java naming convention usually prefers "phoneNumber" (camelCase).
    // If your MongoDB field is named "phoneNumber", this might return null 
    // because "phonenumber" != "phoneNumber".
    // Consider using @Field("phoneNumber") if the DB uses camelCase.
    private String phonenumber; 

    private String status;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime scanTime;
    
    // This is valid if your tracking events update the delivery date
    private LocalDate estimatedDeliveryDate; 
}