package com.warren.clob.services;

import com.warren.clob.models.Order;
import com.warren.clob.repos.OrderRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;

    List<Order> getOrdersByClientId(Long clientId) {
        return orderRepo.findAllByClientId(clientId);
    }

    void deleteOrderById(long id) {
        // Pass to C++ engine for order deletion
    }
}
