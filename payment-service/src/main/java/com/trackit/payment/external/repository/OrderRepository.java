package com.trackit.payment.external.repository;

import com.trackit.payment.external.entity.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<OrderDocument, String> {
    Optional<OrderDocument> findByOrderId(String orderId);
}
