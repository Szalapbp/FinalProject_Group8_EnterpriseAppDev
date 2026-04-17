package com.bearcat.store_api.repositories;
import com.bearcat.store_api.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;



public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}