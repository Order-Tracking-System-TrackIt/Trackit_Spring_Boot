package com.trackit.order.repository;

import com.trackit.order.entity.Order;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

@Repository 
public interface OrderRepository extends MongoRepository<Order, String> {

    Optional<Order> findByOrderId(String orderId);

    List<Order> findByEmail(String email);

    List<Order> findByPhonenumber(String phonenumber);
}
