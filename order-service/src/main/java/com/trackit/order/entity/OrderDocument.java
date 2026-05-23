package com.trackit.order.entity;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders_db")
public class OrderDocument {
    @Id
    private String id;
    private String orderId;
    private String email;
    private String senderName;
    private String senderAddress;
    private String recipientName;
    private String recipientAddress;
    private Double weight;
    private Double deliveryCharge;
    private String status; 
    private String paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}