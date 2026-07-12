package com.warren.clob.controllers;

import com.warren.clob.models.Order;
import com.warren.clob.models.User;
import com.warren.clob.services.OrderService;
import com.warren.clob.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
public class OrderController {
    final OrderService orderService;
    final UserService userService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> getOrdersByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(orderService.getOrdersByClientId(clientId));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addOrder(@RequestBody Order order, Principal principal) {
        String email = principal.getName();
        User verifiedUser = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        order.setClientId(verifiedUser.getId());
        orderService.addOrder(order);
        return ResponseEntity.ok("Order submitted successfully");
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<String> editOrder(@PathVariable Long id, @RequestBody Order order,  Principal principal) {
        String email = principal.getName();
        User verifiedUser = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Order existingOrder = orderService.getOrderById(id);
        if (existingOrder == null) {
            return ResponseEntity.badRequest().body("Order not found");
        }
        if (!existingOrder.getClientId().equals(verifiedUser.getId())) {
            return ResponseEntity.status(403).body("You are not authorized to edit this order");
        }
        order.setClientId(verifiedUser.getId());
        orderService.editOrder(id, order);
        return ResponseEntity.ok("Order edited successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id, Principal principal) {
        String email = principal.getName();
        User verifiedUser = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Order existingOrder = orderService.getOrderById(id);
        if (existingOrder == null) {
            return ResponseEntity.badRequest().body ("Order not found");
        }
        if (!existingOrder.getClientId().equals(verifiedUser.getId())) {
            return ResponseEntity.status(403).body("You are not authorized to delete this order");
        }
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }
}
