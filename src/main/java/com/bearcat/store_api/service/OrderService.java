package com.bearcat.store_api.service;

import com.bearcat.store_api.entities.Order;
import com.bearcat.store_api.entities.OrderItem;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Long userId, String shippingAddress, List<OrderItem> items);
    Optional<Order> getOrderById(Long id);
    List<Order> getUserOrders(Long userId);
}