package com.trackit.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incident_reports")
public class Report {
    @Id
    private String id;
    private String issueType;
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW
    private String orderNumber;
    private String subject;
    private String description;
    private List<String> attachmentUrls;
    private String userId;
    private LocalDateTime createdAt;
    private String status; // LOGGED, IN_REVIEW, DISPATCHED, RESOLVED
}
