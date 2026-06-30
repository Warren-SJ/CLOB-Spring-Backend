package com.warren.clob.services;

import com.warren.clob.models.Order;
import com.warren.clob.repos.OrderRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
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

    public void executeOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getTicker() == null || order.getClientId() == null
                || order.getPrice() == null || order.getOriginalQuantity() == null
                || order.getSide() == '\0') {
            throw new IllegalArgumentException("Order must include ticker, clientId, side, price, and originalQuantity");
        }

        try {
            URL url = URI.create("http://localhost:8081/api/order/add").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            String payload = String.format(
                    Locale.ROOT,
                    "{\"ticker\":%d,\"clientId\":%d,\"side\":\"%s\",\"price\":%d,\"originalQuantity\":%d}",
                    order.getTicker(),
                    order.getClientId(),
                    order.getSide(),
                    order.getPrice(),
                    order.getOriginalQuantity()
            );

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                throw new IllegalStateException("Failed to forward order to matching engine. HTTP " + responseCode);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to forward order to matching engine", e);
        }
    }
}
