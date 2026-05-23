package com.trackit.order.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.trackit.order.dto.OrderDto;
import com.trackit.order.entity.Order;
import com.trackit.order.repository.OrderRepository;
import com.trackit.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private static final double BASE_PRICE = 50.0;
    private static final double PRICE_PER_KG = 20.0;
    private static final double MINIMUM_AMOUNT = 50.0;

    private Double calculateAmount(Double weight) {
        if (weight == null || weight <= 0) {
            return MINIMUM_AMOUNT;
        }
        double amount = BASE_PRICE + (weight * PRICE_PER_KG);
        return Math.round(amount * 100.0) / 100.0;
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        
        // 🔍 Check if order with same orderId already exists
        Optional<Order> existingOrderOpt = orderRepository.findByOrderId(orderDto.getOrderId());
        
        Order orderToSave;
        boolean isUpdate = false;
        
        if (existingOrderOpt.isPresent()) {
            // ✅ UPDATE existing order - auto-update scanTime, location, coordinates
            Order existingOrder = existingOrderOpt.get();
            isUpdate = true;
            
            log.info("📦 Order {} already exists. Updating tracking information...", orderDto.getOrderId());
            
            // Preserve the original MongoDB _id
            orderToSave = existingOrder;
            
            // Update tracking fields (always update these)
            orderToSave.setScanTime(LocalDateTime.now()); // Auto-update scanTime
            
            // Update location and coordinates if provided
            if (orderDto.getLocation() != null) {
                orderToSave.setLocation(orderDto.getLocation());
            }
            if (orderDto.getLatitude() != null) {
                orderToSave.setLatitude(orderDto.getLatitude());
            }
            if (orderDto.getLongitude() != null) {
                orderToSave.setLongitude(orderDto.getLongitude());
            }
            if (orderDto.getStatus() != null) {
                orderToSave.setStatus(orderDto.getStatus());
            }
            
            // Update other fields if provided
            if (orderDto.getEmail() != null) {
                orderToSave.setEmail(orderDto.getEmail());
            }
            if (orderDto.getPhonenumber() != null) {
                orderToSave.setPhonenumber(orderDto.getPhonenumber());
            }
            if (orderDto.getEstimatedDeliveryDate() != null) {
                orderToSave.setEstimatedDeliveryDate(orderDto.getEstimatedDeliveryDate());
            }
            if (orderDto.getWeight() != null) {
                orderToSave.setWeight(orderDto.getWeight());
                orderToSave.setTotalAmount(calculateAmount(orderDto.getWeight()));
            }
            
        } else {
            // ✅ CREATE new order
            orderToSave = mapToEntity(orderDto);
            
            if (orderToSave.getWeight() != null && orderToSave.getWeight() > 0) {
                orderToSave.setTotalAmount(calculateAmount(orderToSave.getWeight()));
            } else {
                orderToSave.setTotalAmount(MINIMUM_AMOUNT);
            }
            
            // Set scanTime to now if not provided
            if (orderToSave.getScanTime() == null) {
                orderToSave.setScanTime(LocalDateTime.now());
            }
        }

        Order savedOrder = orderRepository.save(orderToSave);
        
        if (isUpdate) {
            log.info("🔄 Order UPDATED: {} | Location: {} | Coordinates: ({}, {}) | ScanTime: {}", 
                savedOrder.getOrderId(),
                savedOrder.getLocation(),
                savedOrder.getLatitude(),
                savedOrder.getLongitude(),
                savedOrder.getScanTime());
        } else {
            log.info("✅ Order CREATED: {} | Weight: {} kg | Amount: ₹{}", 
                savedOrder.getOrderId(), 
                savedOrder.getWeight(),
                savedOrder.getTotalAmount());
        }

        return mapToDto(savedOrder);
    }

    // ... rest of the methods remain the same
    
    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with orderId: " + orderId));

        if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
            order.setTotalAmount(calculateAmount(order.getWeight()));
        }

        return mapToDto(order);
    }

    @Override
    public List<OrderDto> getOrdersByEmail(String email) {
        return orderRepository.findByEmail(email)
                .stream()
                .map(order -> {
                    if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
                        order.setTotalAmount(calculateAmount(order.getWeight()));
                    }
                    return mapToDto(order);
                })
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto updateOrder(String orderId, OrderDto orderDto) {
        Order existingOrder = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with orderId: " + orderId));

        if (orderDto.getEmail() != null) existingOrder.setEmail(orderDto.getEmail());
        if (orderDto.getPhonenumber() != null) existingOrder.setPhonenumber(orderDto.getPhonenumber());
        if (orderDto.getStatus() != null) existingOrder.setStatus(orderDto.getStatus());
        if (orderDto.getLocation() != null) existingOrder.setLocation(orderDto.getLocation());
        if (orderDto.getLatitude() != null) existingOrder.setLatitude(orderDto.getLatitude());
        if (orderDto.getLongitude() != null) existingOrder.setLongitude(orderDto.getLongitude());
        if (orderDto.getEstimatedDeliveryDate() != null) existingOrder.setEstimatedDeliveryDate(orderDto.getEstimatedDeliveryDate());
        
        if (orderDto.getWeight() != null) {
            existingOrder.setWeight(orderDto.getWeight());
            existingOrder.setTotalAmount(calculateAmount(orderDto.getWeight()));
        }

        existingOrder.setScanTime(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(existingOrder);
        return mapToDto(updatedOrder);
    }

    @Override
    public void deleteOrder(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with orderId: " + orderId));
        orderRepository.delete(order);
    }

    private Order mapToEntity(OrderDto dto) {
        return Order.builder()
                .orderId(dto.getOrderId())
                .email(dto.getEmail())
                .phonenumber(dto.getPhonenumber())
                .status(dto.getStatus())
                .location(dto.getLocation())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .scanTime(dto.getScanTime())
                .estimatedDeliveryDate(dto.getEstimatedDeliveryDate())
                .weight(dto.getWeight())
                .totalAmount(dto.getTotalAmount())
                .build();
    }

    private OrderDto mapToDto(Order entity) {
        return OrderDto.builder()
                .orderId(entity.getOrderId())
                .email(entity.getEmail())
                .phonenumber(entity.getPhonenumber())
                .status(entity.getStatus())
                .location(entity.getLocation())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .scanTime(entity.getScanTime())
                .estimatedDeliveryDate(entity.getEstimatedDeliveryDate())
                .weight(entity.getWeight())
                .totalAmount(entity.getTotalAmount())
                .build();
    }
}