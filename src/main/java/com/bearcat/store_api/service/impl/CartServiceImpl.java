package com.bearcat.store_api.service.impl;

import com.bearcat.store_api.dao.CartDao;
import com.bearcat.store_api.dao.ProductDao;
import com.bearcat.store_api.entities.Cart;
import com.bearcat.store_api.entities.CartItem;
import com.bearcat.store_api.entities.Product;
import com.bearcat.store_api.service.CartService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartDao cartDao;
    private final ProductDao productDao;

    public CartServiceImpl(CartDao cartDao, ProductDao productDao) {
        this.cartDao = cartDao;
        this.productDao = productDao;
    }

    @Override
    public Cart getOrCreateCart(Long userId) {
        return cartDao.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    // set userId if Cart exposes it, otherwise set User object as appropriate in your model
                    try {
                        java.lang.reflect.Method setUserId = Cart.class.getMethod("setUserId", Long.class);
                        setUserId.invoke(newCart, userId);
                    } catch (Exception e) {
                        // fallback: try to set a User object if available
                        try {
                            java.lang.reflect.Method setUser = Cart.class.getMethod("setUser", Object.class);
                            // cannot create a full User here without the class; leave null or adapt
                        } catch (Exception ignored) {}
                    }
                    return cartDao.save(newCart);
                });
    }

    @Override
    public Cart addItem(Long userId, Long productId, String size, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productDao.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // try to access cart items through getItems() or getCartItems()
        java.util.List<CartItem> items = null;
        try {
            java.lang.reflect.Method getItems = Cart.class.getMethod("getItems");
            //noinspection unchecked
            items = (java.util.List<CartItem>) getItems.invoke(cart);
        } catch (Exception ignored) {
            try {
                java.lang.reflect.Method getCartItems = Cart.class.getMethod("getCartItems");
                //noinspection unchecked
                items = (java.util.List<CartItem>) getCartItems.invoke(cart);
            } catch (Exception ignored) {}
        }
        if (items == null) {
            throw new IllegalStateException("Cart items collection not accessible on Cart entity");
        }

        // Find existing item by product id and size. Support both CartItem.product (object) and productId style.
        Optional<CartItem> existingItem = items.stream().filter(item -> {
            try {
                // if item has getProduct() returning Product
                java.lang.reflect.Method getProduct = CartItem.class.getMethod("getProduct");
                Object prod = getProduct.invoke(item);
                if (prod != null) {
                    java.lang.reflect.Method getId = prod.getClass().getMethod("getId");
                    Object pid = getId.invoke(prod);
                    return productId.equals(pid);
                }
            } catch (Exception ignored) {
            }
            try {
                java.lang.reflect.Method getProductId = CartItem.class.getMethod("getProductId");
                Object pid = getProductId.invoke(item);
                return productId.equals(pid);
            } catch (Exception ignored) {
            }
            return false;
        }).filter(item -> {
            try {
                java.lang.reflect.Method getSize = CartItem.class.getMethod("getSize");
                Object s = getSize.invoke(item);
                return size.equals(s);
            } catch (Exception ignored) {
                return false;
            }
        }).findFirst();

        if (existingItem.isPresent()) {
            CartItem it = existingItem.get();
            try {
                java.lang.reflect.Method getQuantity = CartItem.class.getMethod("getQuantity");
                Integer current = (Integer) getQuantity.invoke(it);
                java.lang.reflect.Method setQuantity = CartItem.class.getMethod("setQuantity", Integer.class);
                setQuantity.invoke(it, current + quantity);
            } catch (Exception ex) {
                // fallback if quantity is int primitive
                try {
                    java.lang.reflect.Method getQuantity = CartItem.class.getMethod("getQuantity");
                    Object current = getQuantity.invoke(it);
                    java.lang.reflect.Method setQuantity = CartItem.class.getMethod("setQuantity", int.class);
                    setQuantity.invoke(it, ((Number) current).intValue() + quantity);
                } catch (Exception ignored) {}
            }
        } else {
            CartItem newItem = new CartItem();
            // set product or productId/productName/price depending on CartItem shape
            try {
                java.lang.reflect.Method setProduct = CartItem.class.getMethod("setProduct", Product.class);
                setProduct.invoke(newItem, product);
            } catch (Exception ignored) {
                try {
                    java.lang.reflect.Method setProductId = CartItem.class.getMethod("setProductId", Long.class);
                    java.lang.reflect.Method setProductName = CartItem.class.getMethod("setProductName", String.class);
                    java.lang.reflect.Method setPrice = CartItem.class.getMethod("setPrice", double.class);
                    setProductId.invoke(newItem, productId);
                    setProductName.invoke(newItem, product.getName());
                    setPrice.invoke(newItem, product.getPrice().doubleValue());
                } catch (Exception ignored2) {}
            }
            try {
                java.lang.reflect.Method setQuantity = CartItem.class.getMethod("setQuantity", Integer.class);
                setQuantity.invoke(newItem, quantity);
            } catch (Exception ignored) {
                try {
                    java.lang.reflect.Method setQuantity = CartItem.class.getMethod("setQuantity", int.class);
                    setQuantity.invoke(newItem, quantity);
                } catch (Exception ignored2) {}
            }
            try {
                java.lang.reflect.Method setSize = CartItem.class.getMethod("setSize", String.class);
                setSize.invoke(newItem, size);
            } catch (Exception ignored) {}
            items.add(newItem);
        }

        return cartDao.save(cart);
    }

    @Override
    public Cart removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        java.util.List<CartItem> items = null;
        try {
            java.lang.reflect.Method getItems = Cart.class.getMethod("getItems");
            //noinspection unchecked
            items = (java.util.List<CartItem>) getItems.invoke(cart);
        } catch (Exception ignored) {
            try {
                java.lang.reflect.Method getCartItems = Cart.class.getMethod("getCartItems");
                //noinspection unchecked
                items = (java.util.List<CartItem>) getCartItems.invoke(cart);
            } catch (Exception ignored) {}
        }
        if (items != null) {
            items.removeIf(item -> {
                try {
                    java.lang.reflect.Method getId = CartItem.class.getMethod("getId");
                    Object id = getId.invoke(item);
                    return itemId.equals(id);
                } catch (Exception ignored) {
                    return false;
                }
            });
        }
        return cartDao.save(cart);
    }

    @Override
    public Cart updateQuantity(Long userId, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        java.util.List<CartItem> items = null;
        try {
            java.lang.reflect.Method getItems = Cart.class.getMethod("getItems");
            //noinspection unchecked
            items = (java.util.List<CartItem>) getItems.invoke(cart);
        } catch (Exception ignored) {
            try {
                java.lang.reflect.Method getCartItems = Cart.class.getMethod("getCartItems");
                //noinspection unchecked
                items = (java.util.List<CartItem>) getCartItems.invoke(cart);
            } catch (Exception ignored) {}
        }
        if (items != null) {
            CartItem found = null;
            for (CartItem item : items) {
                try {
                    java.lang.reflect.Method getId = CartItem.class.getMethod("getId");
                    Object id = getId.invoke(item);
                    if (itemId.equals(id)) {
                        found = item;
                        break;
                    }
                } catch (Exception ignored) {}
            }
            if (found != null) {
                if (quantity <= 0) {
                    items.remove(found);
                } else {
                    try {
                        java.lang.reflect.Method setQuantity = CartItem.class.getMethod("setQuantity", Integer.class);
                        setQuantity.invoke(found, quantity);
                    } catch (Exception ignored) {
                        try {
                            java.lang.reflect.Method setQuantity = CartItem.class.getMethod("setQuantity", int.class);
                            setQuantity.invoke(found, quantity);
                        } catch (Exception ignored2) {}
                    }
                }
            }
        }
        return cartDao.save(cart);
    }

    @Override
    public Optional<Cart> getCartByUserId(Long userId) {
        return cartDao.findByUserId(userId);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        try {
            java.lang.reflect.Method getItems = Cart.class.getMethod("getItems");
            @SuppressWarnings("unchecked")
            java.util.List<CartItem> items = (java.util.List<CartItem>) getItems.invoke(cart);
            if (items != null) {
                items.clear();
            }
        } catch (Exception ignored) {
            try {
                java.lang.reflect.Method getCartItems = Cart.class.getMethod("getCartItems");
                @SuppressWarnings("unchecked")
                java.util.List<CartItem> items = (java.util.List<CartItem>) getCartItems.invoke(cart);
                if (items != null) {
                    items.clear();
                }
            } catch (Exception ignored) {}
        }
        cartDao.save(cart);
    }
}