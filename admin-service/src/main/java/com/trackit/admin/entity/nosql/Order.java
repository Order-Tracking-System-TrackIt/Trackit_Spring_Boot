// entity/nosql/Order.java
package com.trackit.admin.entity.nosql;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime scanTime;
    private LocalDate estimatedDeliveryDate;
    private Double weight;
    private Double totalAmount;
}