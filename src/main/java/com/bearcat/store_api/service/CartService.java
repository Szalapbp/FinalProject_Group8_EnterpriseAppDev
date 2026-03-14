package com.bearcat.store_api.service;

import com.bearcat.store_api.entities.Cart;
import java.util.Optional;
import java.util.UUID;

public interface CartService {
    Cart getOrCreateCart(UUID userId);
    Cart addItem(UUID userId, Long productId, String size, int quantity);
    Cart removeItem(UUID userId, Long itemId);
    Cart updateQuantity(UUID userId, Long itemId, int quantity);
    Optional<Cart> getCartByUserId(UUID userId);
    void clearCart(UUID userId);
}