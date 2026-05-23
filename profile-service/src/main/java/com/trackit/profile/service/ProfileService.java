package com.trackit.profile.service;

import com.trackit.profile.model.UserProfile;
import com.trackit.profile.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    
    @Autowired
    private ProfileRepository profileRepository;
    
    // Create or Update Profile
    public UserProfile saveProfile(UserProfile profile) {
        // Check if profile exists by email
        Optional<UserProfile> existingProfile = profileRepository.findByEmail(profile.getEmail());
        
        if (existingProfile.isPresent()) {
            // Update existing profile
            UserProfile existing = existingProfile.get();
            existing.setFirstName(profile.getFirstName());
            existing.setLastName(profile.getLastName());
            existing.setPhoneNumber(profile.getPhoneNumber());
            existing.setGeographicData(profile.getGeographicData());
            if (profile.getSecurityClearanceLevel() != null) {
                existing.setSecurityClearanceLevel(profile.getSecurityClearanceLevel());
            }
            existing.setUpdatedAt(LocalDateTime.now());
            return profileRepository.save(existing);
        } else {
            // Create new profile
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());
            if (profile.getSecurityClearanceLevel() == null) {
                profile.setSecurityClearanceLevel("customer");
            }
            return profileRepository.save(profile);
        }
    }
    
    // Get Profile by Email (Login)
    public Optional<UserProfile> getProfileByEmail(String email) {
        return profileRepository.findByEmail(email);
    }
    
    // Get Profile by ID
    public Optional<UserProfile> getProfileById(String id) {
        return profileRepository.findById(id);
    }
    
    // Get All Profiles
    public List<UserProfile> getAllProfiles() {
        return profileRepository.findAll();
    }
    
    // Update Profile by ID
    public UserProfile updateProfile(String id, UserProfile profileDetails) {
        return profileRepository.findById(id)
            .map(profile -> {
                profile.setFirstName(profileDetails.getFirstName());
                profile.setLastName(profileDetails.getLastName());
                profile.setPhoneNumber(profileDetails.getPhoneNumber());
                profile.setGeographicData(profileDetails.getGeographicData());
                if (profileDetails.getSecurityClearanceLevel() != null) {
                    profile.setSecurityClearanceLevel(profileDetails.getSecurityClearanceLevel());
                }
                profile.setUpdatedAt(LocalDateTime.now());
                return profileRepository.save(profile);
            })
            .orElseThrow(() -> new RuntimeException("Profile not found with id: " + id));
    }
    
    // Update Profile by Email
    public UserProfile updateProfileByEmail(String email, UserProfile profileDetails) {
        return profileRepository.findByEmail(email)
            .map(profile -> {
                profile.setFirstName(profileDetails.getFirstName());
                profile.setLastName(profileDetails.getLastName());
                profile.setPhoneNumber(profileDetails.getPhoneNumber());
                profile.setGeographicData(profileDetails.getGeographicData());
                if (profileDetails.getSecurityClearanceLevel() != null) {
                    profile.setSecurityClearanceLevel(profileDetails.getSecurityClearanceLevel());
                }
                profile.setUpdatedAt(LocalDateTime.now());
                return profileRepository.save(profile);
            })
            .orElseThrow(() -> new RuntimeException("Profile not found with email: " + email));
    }
    
    // Delete Profile
    public void deleteProfile(String id) {
        if (!profileRepository.existsById(id)) {
            throw new RuntimeException("Profile not found with id: " + id);
        }
        profileRepository.deleteById(id);
    }
    
    // Check if email exists
    public boolean emailExists(String email) {
        return profileRepository.existsByEmail(email);
    }
}