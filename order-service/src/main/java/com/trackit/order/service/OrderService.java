package com.trackit.order.service;

import com.trackit.order.dto.OrderDto;
import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDto);

    OrderDto getOrderByOrderId(String orderId);

    List<OrderDto> getOrdersByEmail(String email);

    OrderDto updateOrder(String orderId, OrderDto orderDto);

    void deleteOrder(String orderId);
}
