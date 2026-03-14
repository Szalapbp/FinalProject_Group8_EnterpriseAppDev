package com.bearcat.store_api.dao.stub;

import com.bearcat.store_api.dao.CartDao;
import com.bearcat.store_api.entities.Cart;
import com.bearcat.store_api.entities.CartItem;
import com.bearcat.store_api.entities.Product;
import com.bearcat.store_api.entities.User;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory stub for CartDao.
 * Useful for unit tests or local development.
 */
@Repository
public class CartDaoStub implements CartDao {
    private final Map<Long, Cart> store = new ConcurrentHashMap<>();
    private long nextCartId = 1;
    private long nextItemId = 1;

    public CartDaoStub() {

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

        Cart cart = new Cart();
        cart.setId(nextCartId++);
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());

        CartItem item = new CartItem();
        item.setId(nextItemId++);
        item.setCart(cart);
        item.setProduct(product);

        item.setQuantity(2);
        item.setSize("L");

        cart.getCartItems().add(item);

        store.put(cart.getId(), cart);
    }

    @Override
    public Optional<Cart> findByUserId(UUID userId) {
        return store.values().stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(userId))
                .findFirst();
    }

    public Cart save(Cart cart) {
        if (cart.getId() == null) {
            cart.setId(nextCartId++);


            if (cart.getCartItems() == null) {
                cart.setCartItems(new ArrayList<>());
            }

            for (CartItem item : cart.getCartItems()) {
                if (item.getId() == null) {
                    item.setId(nextItemId++);
                }

                if (item.getCart() != cart) {
                    item.setCart(cart);
                }
            }
        }
        store.put(cart.getId(), cart);
        return cart;
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}