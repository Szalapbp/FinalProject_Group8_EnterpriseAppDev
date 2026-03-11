package com.bearcat.store_api.dao.stub;

import com.bearcat.store_api.dao.OrderDao;
import com.bearcat.store_api.entities.Order;
import com.bearcat.store_api.entities.OrderItem;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class OrderDaoStub implements OrderDao {
    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private long nextOrderId = 1;
    private long nextItemId = 1;

    public OrderDaoStub() {
        Order order = new Order();
        order.setId(nextOrderId++);
        order.setUserId(1L);
        order.setTotalAmount(99.98);
        order.setShippingAddress("123 Main St, Cincinnati, OH");

        OrderItem item = new OrderItem();
        item.setId(nextItemId++);
        item.setProductId(1L);
        item.setProductName("Bearcats Hoodie");
        item.setPrice(49.99);
        item.setQuantity(2);
        item.setSize("L");
        order.getItems().add(item);

        store.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return store.values().stream()
                .filter(o -> o.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(nextOrderId++);

            for (OrderItem item : order.getItems()) {
                if (item.getId() == null) {
                    item.setId(nextItemId++);
                }
            }
        }
        store.put(order.getId(), order);
        return order;
    }
}