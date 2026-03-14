package com.bearcat.store_api.service;

import com.bearcat.store_api.dao.CartDao;
import com.bearcat.store_api.dao.ProductDao;
import com.bearcat.store_api.entities.Cart;
import com.bearcat.store_api.entities.CartItem;
import com.bearcat.store_api.entities.Product;
import com.bearcat.store_api.entities.User;
import com.bearcat.store_api.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartDao cartDao;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private Product product;
    private User user;
    private UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private Long productId = 100L;
    private Long cartId = 10L;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setFullName("Test User");

        cart = new Cart();
        cart.setId(cartId);
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());

        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("29.99"));
    }

    @Test
    void given_existingCart_when_getOrCreateCart_then_returnsExistingCart() {
        // Given
        when(cartDao.findByUserId(userId)).thenReturn(Optional.of(cart));

        // When
        Cart result = cartService.getOrCreateCart(userId);

        // Then
        assertEquals(cart, result);
        verify(cartDao).findByUserId(userId);
        verify(cartDao, never()).save(any());
    }

    @Test
    void given_noCart_when_getOrCreateCart_then_createsNewCart() {
        // Given
        when(cartDao.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartDao.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart newCart = invocation.getArgument(0);
            newCart.setId(99L);
            return newCart;
        });

        // When
        Cart result = cartService.getOrCreateCart(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUser().getId());
        verify(cartDao).findByUserId(userId);
        verify(cartDao).save(any(Cart.class));
    }

    @Test
    void given_newItem_when_addItem_then_addsToCart() {
        // Given
        String size = "M";
        int quantity = 2;

        when(cartDao.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productDao.findById(productId)).thenReturn(Optional.of(product));
        when(cartDao.save(any(Cart.class))).thenReturn(cart);

        // When
        Cart result = cartService.addItem(userId, productId, size, quantity);

        // Then
        assertEquals(1, result.getCartItems().size());
        CartItem added = result.getCartItems().get(0);
        assertEquals(productId, added.getProduct().getId());
        assertEquals(size, added.getSize());
        assertEquals(quantity, added.getQuantity());
        verify(cartDao).findByUserId(userId);
        verify(productDao).findById(productId);
        verify(cartDao).save(cart);
    }

    @Test
    void given_existingItem_when_addItem_then_incrementsQuantity() {
        // Given
        String size = "M";
        int initialQuantity = 2;
        int additionalQuantity = 3;

        CartItem existingItem = new CartItem();
        existingItem.setId(5L);
        existingItem.setProduct(product);
        existingItem.setSize(size);
        existingItem.setQuantity(initialQuantity);
        existingItem.setCart(cart);
        cart.getCartItems().add(existingItem);

        when(cartDao.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productDao.findById(productId)).thenReturn(Optional.of(product));
        when(cartDao.save(any(Cart.class))).thenReturn(cart);

        // When
        Cart result = cartService.addItem(userId, productId, size, additionalQuantity);

        // Then
        assertEquals(1, result.getCartItems().size());
        assertEquals(initialQuantity + additionalQuantity, result.getCartItems().get(0).getQuantity());
        verify(cartDao).save(cart);

    }


    @Test
    void given_invalidProduct_when_addItem_then_throwsException() {
        // Given
        Long invalidProductId = 999L;
        when(cartDao.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productDao.findById(invalidProductId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () ->
                cartService.addItem(userId, invalidProductId, "M", 1)
        );
        verify(cartDao).findByUserId(userId);
        verify(productDao).findById(invalidProductId);
        verify(cartDao, never()).save(any());
    }

    @Test
    void given_validItem_when_removeItem_then_removesItem() {
        // Given
        Long itemId = 5L;

        CartItem item = new CartItem();
        item.setId(itemId);
        item.setCart(cart);
        cart.getCartItems().add(item);

        when(cartDao.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartDao.save(any(Cart.class))).thenReturn(cart);

        // When
        Cart result = cartService.removeItem(userId, itemId);

        // Then
        assertTrue(result.getCartItems().isEmpty());
        verify(cartDao).save(cart);
    }

    @Test
    void given_nonExistingItem_when_removeItem_then_doesNothing() {
        // Given
        Long nonExistingItemId = 999L;
        when(cartDao.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartDao.save(any(Cart.class))).thenReturn(cart);

        // When
        Cart result = cartService.removeItem(userId, nonExistingItemId);

        // Then
        assertTrue(result.getCartItems().isEmpty());
        verify(cartDao).save(cart);
    }

    @Test
    void given_validItem_when_updateQuantity_then_updatesQuantity() {
        // Given
        Long itemId = 5L;
        int newQuantity = 5;

        CartItem item = new CartItem();
        item.setId(itemId);
        item.setQuantity(2);
        item.setCart(cart);
        cart.getCartItems().add(item);

        when(cartDao.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartDao.save(any(Cart.class))).thenReturn(cart);

        // When
        Cart result = cartService.updateQuantity(userId, itemId, newQuantity);

        // Then
        assertEquals(1, result.getCartItems().size());
        assertEquals(newQuantity, result.getCartItems().get(0).getQuantity());
        verify(cartDao).save(cart);
    }

    @Test
    void given_zeroQuantity_when_updateQuantity_then_removesItem() {
        // Given
        Long itemId = 5L;
        int newQuantity = 0;

        CartItem item = new CartItem();
        item.setId(itemId);
        item.setQuantity(2);
        item.setCart(cart);
        cart.getCartItems().add(item);

        when(cartDao.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartDao.save(any(Cart.class))).thenReturn(cart);

        // When
        Cart result = cartService.updateQuantity(userId, itemId, newQuantity);

        // Then
        assertTrue(result.getCartItems().isEmpty());
        verify(cartDao).save(cart);
    }

    @Test
    void given_existingUser_when_getCartByUserId_then_returnsCart() {
        // Given
        when(cartDao.findByUserId(userId)).thenReturn(Optional.of(cart));

        // When
        Optional<Cart> result = cartService.getCartByUserId(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUser().getId());
        verify(cartDao).findByUserId(userId);
    }

    @Test
    void given_nonExistingUser_when_getCartByUserId_then_returnsEmpty() {
        // Given
        UUID nonExistingUser = UUID.fromString("223e4567-e89b-12d3-a456-426614174001");
        when(cartDao.findByUserId(nonExistingUser)).thenReturn(Optional.empty());

        // When
        Optional<Cart> result = cartService.getCartByUserId(nonExistingUser);

        // Then
        assertFalse(result.isPresent());
        verify(cartDao).findByUserId(nonExistingUser);
    }

    @Test
    void given_cartWithItems_when_clearCart_then_removesAllItems() {
        // Given
        CartItem item1 = new CartItem();
        item1.setId(1L);
        item1.setCart(cart);
        CartItem item2 = new CartItem();
        item2.setId(2L);
        item2.setCart(cart);
        cart.getCartItems().add(item1);
        cart.getCartItems().add(item2);

        when(cartDao.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartDao.save(any(Cart.class))).thenReturn(cart);

        // When
        cartService.clearCart(userId);

        // Then
        assertTrue(cart.getCartItems().isEmpty());
        verify(cartDao).save(cart);
    }
}