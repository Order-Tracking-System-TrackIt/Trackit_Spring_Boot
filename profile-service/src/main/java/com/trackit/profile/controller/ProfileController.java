package com.trackit.profile.controller;

import com.trackit.profile.model.UserProfile;
import com.trackit.profile.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ProfileController {
    
    @Autowired
    private ProfileService profileService;
    
    // Create or Update Profile (POST)
    @PostMapping
    public ResponseEntity<?> createOrUpdateProfile(@Valid @RequestBody UserProfile profile) {
        try {
            UserProfile savedProfile = profileService.saveProfile(profile);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile saved successfully");
            response.put("data", savedProfile);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error saving profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Get Profile by Email (Login endpoint)
    @GetMapping("/login")
    public ResponseEntity<?> loginByEmail(@RequestParam String email) {
        return profileService.getProfileByEmail(email)
            .map(profile -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("exists", true);
                response.put("message", "Profile retrieved successfully");
                response.put("data", profile);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("exists", false);
                response.put("message", "Profile not found for email: " + email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }
    
    // Get Profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable String id) {
        return profileService.getProfileById(id)
            .map(profile -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", profile);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Profile not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            });
    }
    
    // Get All Profiles
    @GetMapping
    public ResponseEntity<?> getAllProfiles() {
        List<UserProfile> profiles = profileService.getAllProfiles();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", profiles);
        response.put("count", profiles.size());
        return ResponseEntity.ok(response);
    }
    
    // Update Profile by ID (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String id, 
            @Valid @RequestBody UserProfile profileDetails) {
        try {
            UserProfile updatedProfile = profileService.updateProfile(id, profileDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile updated successfully");
            response.put("data", updatedProfile);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    // Update Profile by Email (PUT) - Creates if not exists
    @PutMapping("/email/{email}")
    public ResponseEntity<?> updateProfileByEmail(
            @PathVariable String email, 
            @Valid @RequestBody UserProfile profileDetails) {
        try {
            // Check if profile exists
            boolean exists = profileService.emailExists(email);
            
            UserProfile savedProfile;
            String message;
            
            if (exists) {
                // Update existing profile
                savedProfile = profileService.updateProfileByEmail(email, profileDetails);
                message = "Profile updated successfully";
            } else {
                // Create new profile
                profileDetails.setEmail(email);
                savedProfile = profileService.saveProfile(profileDetails);
                message = "Profile created successfully";
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", message);
            response.put("data", savedProfile);
            response.put("created", !exists);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Delete Profile by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable String id) {
        try {
            profileService.deleteProfile(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    // Check if email exists
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = profileService.emailExists(email);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("exists", exists);
        response.put("email", email);
        return ResponseEntity.ok(response);
    }
}