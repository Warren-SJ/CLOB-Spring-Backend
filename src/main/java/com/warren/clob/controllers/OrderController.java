package com.warren.clob.controllers;

import com.warren.clob.models.Order;
import com.warren.clob.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class OrderController {
    final OrderService orderService;

    @PostMapping("api/order/add")
    public ResponseEntity<String> executeOrder(@RequestBody Order order) {
        orderService.executeOrder(order);
        return ResponseEntity.ok("Order submitted successfully");
    }
}
