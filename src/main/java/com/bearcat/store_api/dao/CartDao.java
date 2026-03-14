package com.bearcat.store_api.dao;

import com.bearcat.store_api.entities.Cart;
import java.util.Optional;
import java.util.UUID;

public interface CartDao {
    Optional<Cart> findByUserId(UUID userId);
    Cart save(Cart cart);
    void deleteById(Long id);
}