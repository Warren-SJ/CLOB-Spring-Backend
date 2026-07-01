package com.warren.clob.controllers;

import com.warren.clob.models.Order;
import com.warren.clob.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class OrderController {
    final OrderService orderService;

    @GetMapping("api/order/client/{clientId}")
    public ResponseEntity<?> getOrdersByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(orderService.getOrdersByClientId(clientId));
    }

    @PostMapping("api/order/add")
    public ResponseEntity<String> addOrder(@RequestBody Order order) {
        orderService.addOrder(order);
        return ResponseEntity.ok("Order submitted successfully");
    }

    @PatchMapping("api/order/edit/{id}")
    public ResponseEntity<String> editOrder(@PathVariable Long id, @RequestBody Order order) {
        orderService.editOrder(id, order);
        return ResponseEntity.ok("Order updated successfully");
    }

    @DeleteMapping("api/order/delete/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }
}
