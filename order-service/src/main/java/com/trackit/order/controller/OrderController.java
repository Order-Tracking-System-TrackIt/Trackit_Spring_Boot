package com.trackit.order.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional; // ✅ ADD THIS

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trackit.order.dto.OrderDto;
import com.trackit.order.entity.Order;
import com.trackit.order.repository.OrderRepository;
import com.trackit.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        OrderDto savedOrder = orderService.createOrder(orderDto);
        return ResponseEntity.status(201).body(savedOrder);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderByOrderId(orderId));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<OrderDto>> getOrdersByUser(@PathVariable String email) {
        return ResponseEntity.ok(orderService.getOrdersByEmail(email));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDto> updateOrder(
            @PathVariable String orderId,
            @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderDto));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    // ✅ QR Scanner - auto update MongoDB
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateByQRScan(
            @PathVariable String orderId,
            @RequestBody Map<String, Object> updates) {

        // ✅ Use if/else instead of .map().orElse() to avoid type conflict
        Optional<Order> orderOptional = orderRepository.findByOrderId(orderId);

        if (orderOptional.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Order not found: " + orderId));
        }

        Order order = orderOptional.get();

        if (updates.get("status") != null)
            order.setStatus(updates.get("status").toString());

        if (updates.get("location") != null)
            order.setLocation(updates.get("location").toString());

        if (updates.get("latitude") != null)
            order.setLatitude(Double.valueOf(updates.get("latitude").toString()));

        if (updates.get("longitude") != null)
            order.setLongitude(Double.valueOf(updates.get("longitude").toString()));

        // ✅ Always set server time
        order.setScanTime(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        return ResponseEntity.ok(saved);
    }
}