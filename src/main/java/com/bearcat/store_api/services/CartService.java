package com.bearcat.store_api.services;
import com.bearcat.store_api.entities.Cart;
import com.bearcat.store_api.entities.CartItem;
import com.bearcat.store_api.entities.User;
import com.bearcat.store_api.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())  // Use ID instead
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    public List<Map<String, Object>> mapCartItems(List<CartItem> items) {
        return items.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("productId", item.getProduct().getId());
            map.put("name", item.getProduct().getName());
            map.put("price", item.getProduct().getPrice());
            List<String> images = item.getProduct().getImages();
            map.put("image", images != null && !images.isEmpty() ? images.get(0) : null);
            map.put("size", item.getSize());
            map.put("quantity", item.getQuantity());
            return map;
        }).toList();
    }

    public Map<String, Object> getCartResponse(Cart cart) {
        List<CartItem> items = cart.getCartItems();

        Map<String, Object> response = new HashMap<>();
        response.put("items", mapCartItems(items));
        response.put("totalItems", items.stream()
                .mapToInt(CartItem::getQuantity).sum());

        // Fixed: Use BigDecimal for proper multiplication
        BigDecimal totalPrice = items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.put("totalPrice", totalPrice);

        return response;
    }

}



