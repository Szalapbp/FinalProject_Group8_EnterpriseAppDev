package com.bearcat.store_api.service.impl;

import com.bearcat.store_api.dao.OrderDao;
import com.bearcat.store_api.entities.Order;
import com.bearcat.store_api.entities.OrderItem;
import com.bearcat.store_api.entities.User;
import com.bearcat.store_api.service.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;

    public OrderServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public Order createOrder(UUID userId, String shippingAddress, List<OrderItem> items) {

        User user = new User();
        user.setId(userId);

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setItems(items);

        int total = 0;
        for (OrderItem item : items) {

            total += item.getPrice().multiply(new BigDecimal(item.getQuantity())).intValue();
        }
        order.setTotalAmount(BigDecimal.valueOf(total));

        if (items != null) {
            for (OrderItem item : items) {
                item.setOrder(order);
            }
        }

        return orderDao.save(order);
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderDao.findById(id);
    }

    @Override
    public List<Order> getUserOrders(UUID userId) {
        return orderDao.findByUserId(userId);
    }
}