package com.trackit.payment.client;

import com.trackit.payment.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "order-service",
    url = "${order.service.url}"
)
public interface OrderServiceClient {

    @GetMapping("/api/orders/{orderId}")
    OrderDto getOrderByOrderId(@PathVariable("orderId") String orderId);
}