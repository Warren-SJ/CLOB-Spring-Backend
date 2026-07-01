package com.warren.clob.services;

import com.warren.clob.models.Order;
import com.warren.clob.models.User;
import com.warren.clob.repos.OrderRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final UserService userService;
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public List<Order> getOrdersByClientId(Long clientId) {
        return orderRepo.findAllByClientId(clientId);
    }

    public void addOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getTicker() == null || order.getClientId() == null
                || order.getPrice() == null || order.getOriginalQuantity() == null
                || order.getSide() == '\0') {
            throw new IllegalArgumentException("Order must include ticker, clientId, side, price, and originalQuantity");
        }
        if(order.getPrice() <= 0 || order.getOriginalQuantity() <= 0) {
            throw new IllegalArgumentException("Price and Quantity must be positive");
        }
        int buyingPower = userService.findBuyingPowerById(order.getClientId());
        if (order.getPrice() * order.getOriginalQuantity() > buyingPower) {
            throw new IllegalArgumentException("Insufficient buying power");
        }

        try {
            String payload = String.format(
                    Locale.ROOT,
                    "{\"ticker\":%d,\"clientId\":%d,\"side\":\"%s\",\"price\":%d,\"originalQuantity\":%d}",
                    order.getTicker(),
                    order.getClientId(),
                    order.getSide(),
                    order.getPrice(),
                    order.getOriginalQuantity()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/api/order/add"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Failed to forward order to matching engine. HTTP " + response.statusCode());
            }
            if (response.statusCode() == 200) {
                User existingUser = userService.findById(order.getClientId());
                existingUser.setBuyingPower(existingUser.getBuyingPower() - (order.getPrice() * order.getOriginalQuantity()));
                userService.save(existingUser);
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to forward order to matching engine", e);
        }
    }

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
        if (existingOrder.getSide() == 'B' &&
                buyingPower - (order.getPrice() * order.getOriginalQuantity()) + (existingOrder.getPrice() * existingOrder.getOriginalQuantity()) < 0) {
            throw new IllegalArgumentException("Insufficient buying power");
        }
        try {
            String payload = String.format(
                    Locale.ROOT,
                    "{\"price\":%d,\"originalQuantity\":%d}",
                    order.getPrice(),
                    order.getOriginalQuantity()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/api/order/edit/" + id))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Failed to forward order to matching engine. HTTP " + response.statusCode());
            }
            if(response.statusCode() == 200) {
                if(existingOrder.getSide() == 'B') {
                    User existingUser = userService.findById(existingOrder.getClientId());
                    existingUser.setBuyingPower(existingUser.getBuyingPower() - (order.getPrice() * order.getOriginalQuantity()));
                    existingUser.setBuyingPower(existingUser.getBuyingPower() + (existingOrder.getPrice() * existingOrder.getOriginalQuantity()));
                    userService.save(existingUser);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to forward order to matching engine", e);
        }
    }

    public void deleteOrder(Long id) {
        Order order = orderRepo.findById(id).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/api/order/delete/" + id))
                    .method("DELETE", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Failed to forward order to matching engine. HTTP " + response.statusCode());
            }
            if (response.statusCode() == 200) {
                if (order.getSide() == 'B') {
                    User existingUser = userService.findById(order.getClientId());
                    existingUser.setBuyingPower(existingUser.getBuyingPower() + order.getPrice() * order.getOriginalQuantity());
                    userService.save(existingUser);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to forward order to matching engine", e);
        }
    }
}
