package com.bearcat.store_api.service.impl;

import com.bearcat.store_api.dao.CartDao;
import com.bearcat.store_api.dao.ProductDao;
import com.bearcat.store_api.entities.Cart;
import com.bearcat.store_api.entities.CartItem;
import com.bearcat.store_api.entities.Product;
import com.bearcat.store_api.entities.User;
import com.bearcat.store_api.service.CartService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    private final CartDao cartDao;
    private final ProductDao productDao;

    public CartServiceImpl(CartDao cartDao, ProductDao productDao) {
        this.cartDao = cartDao;
        this.productDao = productDao;
    }

    @Override
    public Cart getOrCreateCart(UUID userId) {
        return cartDao.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();


                    User user = new User();
                    user.setId(userId);
                    newCart.setUser(user);

                    return cartDao.save(newCart);
                });
    }

    @Override
    public Cart addItem(UUID userId, Long productId, String size, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productDao.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId)
                        && item.getSize().equals(size))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setSize(size);
            newItem.setCart(cart);
            cart.getCartItems().add(newItem);
        }

        return cartDao.save(cart);
    }

    @Override
    public Cart removeItem(UUID userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().removeIf(item -> item.getId().equals(itemId));
        return cartDao.save(cart);
    }

    @Override
    public Cart updateQuantity(UUID userId, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .ifPresent(item -> {
                    if (quantity <= 0) {
                        cart.getCartItems().remove(item);
                    } else {
                        item.setQuantity(quantity);
                    }
                });
        return cartDao.save(cart);
    }

    @Override
    public Optional<Cart> getCartByUserId(UUID userId) {
        return cartDao.findByUserId(userId);
    }

    @Override
    public void clearCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().clear();
        cartDao.save(cart);
    }
}
