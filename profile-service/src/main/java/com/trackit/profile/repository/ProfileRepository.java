package com.trackit.profile.repository;

import com.trackit.profile.model.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends MongoRepository<UserProfile, String> {
    
    Optional<UserProfile> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    void deleteByEmail(String email);
}