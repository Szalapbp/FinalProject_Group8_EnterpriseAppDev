package com.bearcat.store_api.dao.stub;

import com.bearcat.store_api.dao.CartDao;
import com.bearcat.store_api.entities.Cart;
import com.bearcat.store_api.entities.CartItem;
import com.bearcat.store_api.entities.Product;
import org.springframework.stereotype.Repository;

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
        // seed one cart for userId = 1
        Cart cart = new Cart();
        cart.setId(nextCartId++);

        // Try to set a user id if your Cart has such field; if not, adapt as needed.
        // If Cart has a User object, set a small stub user there.
        try {
            // reflection-based helper to set userId if the field exists (non-intrusive)
            java.lang.reflect.Method setUserId = Cart.class.getMethod("setUserId", Long.class);
            setUserId.invoke(cart, 1L);
        } catch (Exception ignored) {
            // no setUserId method -> assume cart has a User object or another way; leave unassigned
        }

        // Create an example product and cart item using your entity shapes
        CartItem item = new CartItem();
        item.setId(nextItemId++);
        try {
            // If CartItem uses productId/productName/price style (older DTO-like)
            java.lang.reflect.Method setProductId = CartItem.class.getMethod("setProductId", Long.class);
            java.lang.reflect.Method setProductName = CartItem.class.getMethod("setProductName", String.class);
            java.lang.reflect.Method setPrice = CartItem.class.getMethod("setPrice", double.class);
            java.lang.reflect.Method setQuantity = CartItem.class.getMethod("setQuantity", int.class);
            java.lang.reflect.Method setSize = CartItem.class.getMethod("setSize", String.class);

            setProductId.invoke(item, 1L);
            setProductName.invoke(item, "Bearcats Hoodie");
            setPrice.invoke(item, 49.99);
            setQuantity.invoke(item, 2);
            setSize.invoke(item, "L");

            // add to items list via getItems / setItems
            try {
                java.lang.reflect.Method getItems = Cart.class.getMethod("getItems");
                @SuppressWarnings("unchecked")
                List<CartItem> items = (List<CartItem>) getItems.invoke(cart);
                items.add(item);
            } catch (Exception ex) {
                // fallback to cart.getCartItems if different name
                try {
                    java.lang.reflect.Method getCartItems = Cart.class.getMethod("getCartItems");
                    @SuppressWarnings("unchecked")
                    List<CartItem> items = (List<CartItem>) getCartItems.invoke(cart);
                    items.add(item);
                } catch (Exception ignored) {}
            }
        } catch (NoSuchMethodException e) {
            // If CartItem uses Product object, create product and attach
            Product p = new Product();
            try {
                java.lang.reflect.Method setProductId = Product.class.getMethod("setId", Long.class);
                java.lang.reflect.Method setName = Product.class.getMethod("setName", String.class);
                java.lang.reflect.Method setPrice = Product.class.getMethod("setPrice", java.math.BigDecimal.class);
                setProductId.invoke(p, 1L);
                setName.invoke(p, "Bearcats Hoodie");
                setPrice.invoke(p, java.math.BigDecimal.valueOf(49.99));
            } catch (Exception ignored) {}

            try {
                java.lang.reflect.Method setProduct = CartItem.class.getMethod("setProduct", Product.class);
                java.lang.reflect.Method setQuantity = CartItem.class.getMethod("setQuantity", Integer.class);
                java.lang.reflect.Method setSize = CartItem.class.getMethod("setSize", String.class);
                setProduct.invoke(item, p);
                setQuantity.invoke(item, 2);
                setSize.invoke(item, "L");

                // add to cart's list
                try {
                    java.lang.reflect.Method getCartItems = Cart.class.getMethod("getCartItems");
                    @SuppressWarnings("unchecked")
                    List<CartItem> items = (List<CartItem>) getCartItems.invoke(cart);
                    items.add(item);
                } catch (Exception ignored) {}
            } catch (Exception ignored) {}
        }

        store.put(cart.getId(), cart);
    }

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        // attempt multiple access patterns since Cart shape can vary across projects
        return store.values().stream().filter(c -> {
            try {
                java.lang.reflect.Method getUserId = Cart.class.getMethod("getUserId");
                Object uid = getUserId.invoke(c);
                return Objects.equals(userId, uid);
            } catch (Exception e) {
                // try getUser().getId()
                try {
                    java.lang.reflect.Method getUser = Cart.class.getMethod("getUser");
                    Object user = getUser.invoke(c);
                    if (user == null) return false;
                    java.lang.reflect.Method getId = user.getClass().getMethod("getId");
                    Object uid = getId.invoke(user);
                    return Objects.equals(userId, uid);
                } catch (Exception ex) {
                    return false;
                }
            }
        }).findFirst();
    }

    @Override
    public Cart save(Cart cart) {
        if (cart.getId() == null) {
            cart.setId(nextCartId++);
        }

        // ensure items have ids where applicable
        try {
            java.lang.reflect.Method getItems = Cart.class.getMethod("getItems");
            @SuppressWarnings("unchecked")
            List<CartItem> items = (List<CartItem>) getItems.invoke(cart);
            if (items != null) {
                for (CartItem item : items) {
                    if (item.getId() == null) {
                        item.setId(nextItemId++);
                    }
                    // when CartItem has back-reference to Cart, ensure it's set
                    try {
                        java.lang.reflect.Method setCart = CartItem.class.getMethod("setCart", Cart.class);
                        setCart.invoke(item, cart);
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            // try cart.getCartItems()
            try {
                java.lang.reflect.Method getCartItems = Cart.class.getMethod("getCartItems");
                @SuppressWarnings("unchecked")
                List<CartItem> items = (List<CartItem>) getCartItems.invoke(cart);
                if (items != null) {
                    for (CartItem item : items) {
                        if (item.getId() == null) {
                            item.setId(nextItemId++);
                        }
                        try {
                            java.lang.reflect.Method setCart = CartItem.class.getMethod("setCart", Cart.class);
                            setCart.invoke(item, cart);
                        } catch (Exception ignored) {}
                    }
                }
            } catch (Exception ignored) {}
        }

        store.put(cart.getId(), cart);
        return cart;
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}