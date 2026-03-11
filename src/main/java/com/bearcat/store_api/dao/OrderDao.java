package com.bearcat.store_api.dao;

import com.bearcat.store_api.entities.Order;
import java.util.List;
import java.util.Optional;

public interface OrderDao {
    Optional<Order> findById(Long id);
    List<Order> findByUserId(Long userId);
    Order save(Order order);
}