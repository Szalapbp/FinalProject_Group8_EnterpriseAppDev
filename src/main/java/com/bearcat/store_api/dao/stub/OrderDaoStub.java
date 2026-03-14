package com.bearcat.store_api.dao.stub;

import com.bearcat.store_api.dao.OrderDao;
import com.bearcat.store_api.entities.Order;
import com.bearcat.store_api.entities.OrderItem;
import com.bearcat.store_api.entities.Product;
import com.bearcat.store_api.entities.User;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class OrderDaoStub implements OrderDao {
    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private long nextOrderId = 1;
    private long nextItemId = 1;

    public OrderDaoStub() {

        User user = new User();
        user.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        user.setEmail("test@example.com");
        user.setFullName("Test User");

        Product product = new Product();
        product.setId(1L);
        product.setName("Bearcats Hoodie");
        product.setPrice(new BigDecimal("49.99"));

        product.setCategory("Clothing");
        product.setInStock(true);

        Order order = new Order();
        order.setId(nextOrderId++);
        order.setUser(user);
        order.setTotalAmount(BigDecimal.valueOf(100));
        order.setShippingAddress("123 Main St, Cincinnati, OH");
        order.setItems(new ArrayList<>());

        OrderItem item = new OrderItem();
        item.setId(nextItemId++);
        item.setOrder(order);
        item.setProduct(product);
        item.setProductName(product.getName());
        item.setPrice(product.getPrice());
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
    public List<Order> findByUserId(UUID userId) {
        return store.values().stream()
                .filter(o -> o.getUser() != null && o.getUser().getId().equals(userId))
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