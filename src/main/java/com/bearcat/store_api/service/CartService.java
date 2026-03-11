package com.bearcat.store_api.service;

import com.bearcat.store_api.entities.Cart;
import java.util.Optional;

public interface CartService {
    Cart getOrCreateCart(Long userId);
    Cart addItem(Long userId, Long productId, String size, int quantity);
    Cart removeItem(Long userId, Long itemId);
    Cart updateQuantity(Long userId, Long itemId, int quantity);
    Optional<Cart> getCartByUserId(Long userId);
    void clearCart(Long userId);
}