// repository/nosql/PaymentRepository.java
package com.trackit.admin.repository.nosql;

import com.trackit.admin.entity.nosql.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    
    // Find payments by status
    List<Payment> findByPaymentStatus(String paymentStatus);
    
    // Find completed payments in date range
    @Query("{ 'paymentStatus': 'COMPLETED', 'paymentTime': { $gte: ?0, $lte: ?1 } }")
    List<Payment> findCompletedPaymentsByDateRange(LocalDateTime start, LocalDateTime end);
    
    // Find all payments in date range
    @Query("{ 'paymentTime': { $gte: ?0, $lte: ?1 } }")
    List<Payment> findPaymentsByDateRange(LocalDateTime start, LocalDateTime end);
    
    // Find payments by created date range
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<Payment> findPaymentsByCreatedAtRange(LocalDateTime start, LocalDateTime end);
    
    // Count completed payments
    Long countByPaymentStatus(String paymentStatus);
}