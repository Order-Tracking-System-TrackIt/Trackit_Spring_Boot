package com.trackit.order.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "tracking_events_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String id;

    private String orderId;
    private String email;
    private String phonenumber;
    private String status;
    private String location;
    private Double latitude;
    private Double longitude;
    
    @Builder.Default
    private LocalDateTime scanTime = LocalDateTime.now();
    
    @Builder.Default
    private LocalDate estimatedDeliveryDate = LocalDate.now().plusDays(5);
    

    private Double weight;
    private Double totalAmount; 
}