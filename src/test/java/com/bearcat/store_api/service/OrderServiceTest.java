package com.bearcat.store_api.service;

import com.bearcat.store_api.dao.OrderDao;
import com.bearcat.store_api.entities.Order;
import com.bearcat.store_api.entities.OrderItem;
import com.bearcat.store_api.entities.Product;
import com.bearcat.store_api.entities.User;
import com.bearcat.store_api.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private User user;
    private Long orderId = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());

        order = new Order();
        order.setId(orderId);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setShippingAddress("123 Main St");

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("49.99"));

        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setProduct(product);
        item.setProductName("Test Product");
        item.setPrice(new BigDecimal("49.99"));
        item.setQuantity(2);
        item.setSize("L");
        item.setOrder(order);

        order.getItems().add(item);
    }

    @Test
    void given_validId_when_getOrderById_then_returnsOrder() {
        // Given
        when(orderDao.findById(orderId)).thenReturn(Optional.of(order));

        // When
        Optional<Order> result = orderService.getOrderById(orderId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(orderId, result.get().getId());
        assertEquals(user.getId(), result.get().getUser().getId());
        verify(orderDao).findById(orderId);
    }

    @Test
    void given_invalidId_when_getOrderById_then_returnsEmpty() {
        // Given
        Long invalidId = 999L;
        when(orderDao.findById(invalidId)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderService.getOrderById(invalidId);

        // Then
        assertFalse(result.isPresent());
        verify(orderDao).findById(invalidId);
    }

    @Test
    void given_existingUserId_when_getUserOrders_then_returnsOrders() {
        // Given
        List<Order> orders = List.of(order);
        when(orderDao.findByUserId(user.getId())).thenReturn(orders);

        // When
        List<Order> results = orderService.getUserOrders(user.getId());

        // Then
        assertEquals(1, results.size());
        assertEquals(user.getId(), results.get(0).getUser().getId());
        verify(orderDao).findByUserId(user.getId());
    }

    @Test
    void given_nonExistingUserId_when_getUserOrders_then_returnsEmptyList() {
        // Given
        UUID nonExistingUser = UUID.randomUUID();
        when(orderDao.findByUserId(nonExistingUser)).thenReturn(List.of());

        // When
        List<Order> results = orderService.getUserOrders(nonExistingUser);

        // Then
        assertTrue(results.isEmpty());
        verify(orderDao).findByUserId(nonExistingUser);
    }

    @Test
    void given_validData_when_createOrder_then_returnsOrder() {
        // Given
        UUID newUserId = UUID.randomUUID();
        String address = "456 Oak St";

        List<OrderItem> items = new ArrayList<>();

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("29.99"));

        OrderItem item1 = new OrderItem();
        item1.setProduct(product1);
        item1.setProductName("Product 1");
        item1.setPrice(new BigDecimal("29.99"));
        item1.setQuantity(2);
        item1.setSize("M");
        items.add(item1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("19.99"));

        OrderItem item2 = new OrderItem();
        item2.setProduct(product2);
        item2.setProductName("Product 2");
        item2.setPrice(new BigDecimal("19.99"));
        item2.setQuantity(1);
        item2.setSize("S");
        items.add(item2);

        Order savedOrder = new Order();
        savedOrder.setId(5L);

        User savedUser = new User();
        savedUser.setId(newUserId);
        savedOrder.setUser(savedUser);

        savedOrder.setShippingAddress(address);
        savedOrder.setItems(items);
        savedOrder.setTotalAmount(new BigDecimal("79.97"));

        when(orderDao.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = orderService.createOrder(newUserId, address, items);

        // Then
        assertNotNull(result);
        assertEquals(newUserId, result.getUser().getId());
        assertEquals(address, result.getShippingAddress());
        assertEquals(2, result.getItems().size());
        assertEquals(new BigDecimal("79.97"), result.getTotalAmount());
        verify(orderDao).save(any(Order.class));
    }

    @Test
    void given_emptyItems_when_createOrder_then_returnsOrderWithZeroTotal() {
        // Given
        UUID newUserId = UUID.randomUUID();
        String address = "456 Oak St";
        List<OrderItem> emptyItems = new ArrayList<>();

        User savedUser = new User();
        savedUser.setId(newUserId);

        Order savedOrder = new Order();
        savedOrder.setId(6L);
        savedOrder.setUser(savedUser);
        savedOrder.setShippingAddress(address);
        savedOrder.setTotalAmount(BigDecimal.ZERO);
        savedOrder.setItems(emptyItems);

        when(orderDao.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = orderService.createOrder(newUserId, address, emptyItems);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        assertTrue(result.getItems().isEmpty());
        verify(orderDao).save(any(Order.class));
    }
}