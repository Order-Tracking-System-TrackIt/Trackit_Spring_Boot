// repository/nosql/OrderRepository.java
package com.trackit.admin.repository.nosql;

import com.trackit.admin.entity.nosql.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    // Find by status
    List<Order> findByStatus(String status);
    
    // Count by status
    Long countByStatus(String status);
    
    // Find orders with coordinates for heatmap
    @Query("{ 'latitude': { $ne: null }, 'longitude': { $ne: null } }")
    List<Order> findOrdersWithCoordinates();
    
    // Find orders by scan time range
    @Query("{ 'scanTime': { $gte: ?0, $lte: ?1 } }")
    List<Order> findOrdersByScanTimeRange(LocalDateTime start, LocalDateTime end);
    
    // Find active shipments (IN_TRANSIT, OUT_FOR_DELIVERY)
    @Query("{ 'status': { $in: ['IN_TRANSIT', 'OUT_FOR_DELIVERY'] } }")
    List<Order> findActiveShipments();
    
    // Count active shipments
    @Query(value = "{ 'status': { $in: ['IN_TRANSIT', 'OUT_FOR_DELIVERY'] } }", count = true)
    Long countActiveShipments();
    
    // Find exceptions
    @Query("{ 'status': 'EXCEPTION' }")
    List<Order> findExceptionOrders();
    
    // Find delivered orders
    List<Order> findByStatusIgnoreCase(String status);
}