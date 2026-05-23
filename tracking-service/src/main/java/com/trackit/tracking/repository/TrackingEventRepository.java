package com.trackit.tracking.repository;

import com.trackit.tracking.entity.TrackingEventDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingEventRepository extends MongoRepository<TrackingEventDocument, String> {

    // --- Existing Methods ---
    
    List<TrackingEventDocument> findByOrderIdOrderByScanTimeDesc(String orderId);
    
    Page<TrackingEventDocument> findByOrderId(String orderId, Pageable pageable);
    
    List<TrackingEventDocument> findByEmailOrderByScanTimeDesc(String email);
    
    Page<TrackingEventDocument> findByEmail(String email, Pageable pageable);
    
    Optional<TrackingEventDocument> findTopByOrderIdOrderByScanTimeDesc(String orderId);

    // --- NEW METHODS FOR ORDER ID + PHONE ---

    // 1. Get history for a specific order verified by phone number
    List<TrackingEventDocument> findByOrderIdAndPhonenumberOrderByScanTimeDesc(String orderId, String phonenumber);

    // 2. (Optional) If you want the latest status for that specific combo
    Optional<TrackingEventDocument> findTopByOrderIdAndPhonenumberOrderByScanTimeDesc(String orderId, String phonenumber);
}