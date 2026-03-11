package com.bearcat.store_api.dao;

import com.bearcat.store_api.entities.Cart;
import java.util.Optional;

public interface CartDao {
    Optional<Cart> findByUserId(Long userId);
    Cart save(Cart cart);
    void deleteById(Long id);
}