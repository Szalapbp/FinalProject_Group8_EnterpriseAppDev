package com.bearcat.store_api.service.impl;

import com.bearcat.store_api.dao.OrderDao;
import com.bearcat.store_api.entities.Order;
import com.bearcat.store_api.entities.OrderItem;
import com.bearcat.store_api.service.OrderService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;

    public OrderServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public Order createOrder(Long userId, String shippingAddress, List<OrderItem> items) {
        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(shippingAddress);
        order.setItems(items);

        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        order.setTotalAmount(total);

        return orderDao.save(order);
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderDao.findById(id);
    }

    @Override
    public List<Order> getUserOrders(Long userId) {
        return orderDao.findByUserId(userId);
    }
}