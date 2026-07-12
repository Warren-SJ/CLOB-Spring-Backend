package com.warren.clob.services;

import com.warren.clob.dto.OrderRequest;
import com.warren.clob.models.Order;
import com.warren.clob.models.User;
import com.warren.clob.repos.OrderRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.Locale;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    private final OrderRepo orderRepo;
    private final UserService userService;
    //private static final HttpClient httpClient = HttpClient.newHttpClient();

    public List<Order> getOrdersByClientId(Long clientId) {
        return orderRepo.findAllByClientId(clientId);
    }

    @Transactional
    public void addOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getTicker() == null || order.getClientId() == null
                || order.getPrice() == null || order.getOriginalQuantity() == null
                || order.getSide() == '\0') {
            throw new IllegalArgumentException("Order must include ticker, clientId, side, price, and originalQuantity");
        }
        char side = Character.toUpperCase(order.getSide());
        if (side != 'B' && side != 'S') {
            throw new IllegalArgumentException("Side must be 'B' or 'S'");
        }
        if(order.getPrice() <= 0 || order.getOriginalQuantity() <= 0) {
            throw new IllegalArgumentException("Price and Quantity must be positive");
        }
        User existingUser = userService.findById(order.getClientId());
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }
        long totalRequired = (long) order.getPrice() * order.getOriginalQuantity();
        if (totalRequired > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Order total cost exceeds maximum allowable limit");
        }
        if(side == 'B') {
            int buyingPower = userService.findBuyingPowerById(order.getClientId());
            if (order.getPrice() * order.getOriginalQuantity() > buyingPower) {
                throw new IllegalArgumentException("Insufficient buying power");
            }
        }

        try {
            if (order.getSide() == 'B') {
                existingUser.setBuyingPower(existingUser.getBuyingPower() - (order.getPrice() * order.getOriginalQuantity()));
                userService.save(existingUser);
            }

            OrderRequest addRequest = OrderRequest.builder()
                    .action(OrderRequest.ActionType.ADD)
                    .clientId(order.getClientId())
                    .ticker(order.getTicker())
                    .side(order.getSide())
                    .price(order.getPrice())
                    .quantity(order.getOriginalQuantity())
                    .build();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    rabbitTemplate.convertAndSend("order.requests", addRequest);
                }
            });

//            String payload = String.format(
//                    Locale.ROOT,
//                    "{\"ticker\":%d,\"clientId\":%d,\"side\":\"%s\",\"price\":%d,\"originalQuantity\":%d}",
//                    order.getTicker(),
//                    order.getClientId(),
//                    order.getSide(),
//                    order.getPrice(),
//                    order.getOriginalQuantity()
//            );
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("http://localhost:8081/api/order/add"))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(payload))
//                    .build();
//
//            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() < 200 || response.statusCode() >= 300) {
//                throw new IllegalStateException("Failed to forward order to matching engine. HTTP " + response.statusCode());
//            }
//            if (response.statusCode() == 200) {
//                User existingUser = userService.findById(order.getClientId());
//                existingUser.setBuyingPower(existingUser.getBuyingPower() - (order.getPrice() * order.getOriginalQuantity()));
//                userService.save(existingUser);
//            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to forward order to matching engine", e);
        }
    }

    @Transactional
    public void editOrder(Long id, Order order) {
        if (order == null || id == null) {
            throw new IllegalArgumentException("Order or id cannot be null");
        }
        if (order.getPrice() == null || order.getOriginalQuantity() == null) {
            throw new IllegalArgumentException("Order edit must include price and originalQuantity");
        }
        Order existingOrder = orderRepo.findById(id).orElse(null);
        if (existingOrder == null) {
            throw new IllegalArgumentException("Order not found");
        }
        int buyingPower = userService.findBuyingPowerById(existingOrder.getClientId());
        User existingUser = userService.findById(existingOrder.getClientId());
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }
        long existingRemainingQty = (existingOrder.getRemainingQuantity() != null) ? existingOrder.getRemainingQuantity() : existingOrder.getOriginalQuantity();
        if (existingOrder.getSide() == 'B' &&
                buyingPower - ((long) order.getPrice() * order.getOriginalQuantity()) + (existingOrder.getPrice() * existingRemainingQty) < 0) {
            throw new IllegalArgumentException("Insufficient buying power");
        }
        long newLock = (long) order.getPrice() * order.getOriginalQuantity();
        long oldLock = (long) existingOrder.getPrice() * existingRemainingQty;

        if (newLock > Integer.MAX_VALUE || oldLock > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Order cost exceeds maximum allowable limit");
        }
        try {
            if(existingOrder.getSide() == 'B') {
                existingUser.setBuyingPower(existingUser.getBuyingPower() - (order.getPrice() * order.getOriginalQuantity()));
                existingUser.setBuyingPower(existingUser.getBuyingPower() + (int)(existingOrder.getPrice() * existingRemainingQty));
                userService.save(existingUser);
            }

            OrderRequest editRequest = OrderRequest.builder()
                    .action(OrderRequest.ActionType.EDIT)
                    .orderId(id)
                    .price(order.getPrice())
                    .quantity(order.getOriginalQuantity())
                    .build();

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    rabbitTemplate.convertAndSend("order.requests", editRequest);
                }
            });

//            String payload = String.format(
//                    Locale.ROOT,
//                    "{\"price\":%d,\"originalQuantity\":%d}",
//                    order.getPrice(),
//                    order.getOriginalQuantity()
//            );
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("http://localhost:8081/api/order/edit/" + id))
//                    .header("Content-Type", "application/json")
//                    .method("PATCH", HttpRequest.BodyPublishers.ofString(payload))
//                    .build();
//
//            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() < 200 || response.statusCode() >= 300) {
//                throw new IllegalStateException("Failed to forward order to matching engine. HTTP " + response.statusCode());
//            }
//            if (response.statusCode() == 200) {
//                if (order.getSide() == 'B') {
//                    User existingUser = userService.findById(existingOrder.getClientId());
//                    existingUser.setBuyingPower(existingUser.getBuyingPower() - (order.getPrice() * order.getOriginalQuantity()));
//                    existingUser.setBuyingPower(existingUser.getBuyingPower() + (int)(existingOrder.getPrice() * existingRemainingQty));
//                    userService.save(existingUser);
//                }
//            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to forward order to matching engine", e);
        }
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepo.findById(id).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        try {
            if (order.getSide() == 'B') {
                User existingUser = userService.findById(order.getClientId());
                if (existingUser == null) {
                    throw new IllegalArgumentException("User not found");
                }
                long remainingQty = (order.getRemainingQuantity() != null) ? order.getRemainingQuantity() : order.getOriginalQuantity();
                long refundAmount = (long) order.getPrice() * remainingQty;
                long newBuyingPower = (long) existingUser.getBuyingPower() + refundAmount;
                if (newBuyingPower > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("Refund would exceed maximum buying power");
                }
                existingUser.setBuyingPower(existingUser.getBuyingPower() + (int)(order.getPrice() * remainingQty));
                userService.save(existingUser);
            }

            OrderRequest deleteRequest = OrderRequest.builder()
                    .action(OrderRequest.ActionType.DELETE)
                    .orderId(order.getId())
                    .build();

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    rabbitTemplate.convertAndSend("order.requests", deleteRequest);
                }
            });

//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("http://localhost:8081/api/order/delete/" + id))
//                    .method("DELETE", HttpRequest.BodyPublishers.noBody())
//                    .build();
//
//            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() < 200 || response.statusCode() >= 300) {
//                throw new IllegalStateException("Failed to forward order to matching engine. HTTP " + response.statusCode());
//            }
//            if (response.statusCode() == 200) {
//                if (order.getSide() == 'B') {
//                    User existingUser = userService.findById(order.getClientId());
//                    long remainingQty = (order.getRemainingQuantity() != null) ? order.getRemainingQuantity() : order.getOriginalQuantity();
//                    existingUser.setBuyingPower(existingUser.getBuyingPower() + (int)(order.getPrice() * remainingQty));
//                    userService.save(existingUser);
//                }
//            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to forward order to matching engine", e);
        }
    }

    public Order getOrderById(Long id) {
        return orderRepo.findById(id).orElse(null);
    }
}
