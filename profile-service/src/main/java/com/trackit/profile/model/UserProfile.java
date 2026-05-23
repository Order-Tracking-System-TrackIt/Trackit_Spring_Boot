package com.trackit.profile.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_profiles")
public class UserProfile {
    
    @Id
    private String id;
    
    private String firstName;
    
    private String lastName;
    
    @Indexed(unique = true)
    private String email;
    
    private String phoneNumber;
    
    private String securityClearanceLevel = "customer";
    
    private GeographicData geographicData;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructor for creation
    public UserProfile(String firstName, String lastName, String email, 
                      String phoneNumber, GeographicData geographicData) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.geographicData = geographicData;
        this.securityClearanceLevel = "customer";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}