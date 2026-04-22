package com.bearcat.store_api.repositories;

import com.bearcat.store_api.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}