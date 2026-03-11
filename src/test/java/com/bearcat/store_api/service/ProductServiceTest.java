package com.bearcat.store_api.service;
import com.bearcat.store_api.dao.ProductDao;
import com.bearcat.store_api.entities.Product;
import com.bearcat.store_api.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Bearcats Hoodie");
        product1.setPrice(new BigDecimal("49.99"));
        product1.setCategory("apparel");
        product1.setFeatured(true);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("UC Cap");
        product2.setPrice(new BigDecimal("24.99"));
        product2.setCategory("accessories");
        product2.setFeatured(false);

        product3 = new Product();
        product3.setId(3L);
        product3.setName("Bearcats T-Shirt");
        product3.setPrice(new BigDecimal("29.99"));
        product3.setCategory("apparel");
        product3.setFeatured(true);
    }

    @Test
    void given_validId_when_getProductById_then_returnsProduct() {
        // Given
        Long id = 1L;
        when(productDao.findById(id)).thenReturn(Optional.of(product1));

        // When
        Optional<Product> result = productService.getProductById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Bearcats Hoodie", result.get().getName());
        verify(productDao).findById(id);
    }

    @Test
    void given_invalidId_when_getProductById_then_returnsEmpty() {
        // Given
        Long id = 999L;
        when(productDao.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Product> result = productService.getProductById(id);

        // Then
        assertFalse(result.isPresent());
        verify(productDao).findById(id);
    }

    @Test
    void when_getAllProducts_then_returnsAllProducts() {
        // Given
        List<Product> expectedProducts = Arrays.asList(product1, product2, product3);
        when(productDao.findAll()).thenReturn(expectedProducts);

        // When
        List<Product> results = productService.getAllProducts();

        // Then
        assertEquals(3, results.size());
        verify(productDao).findAll();
    }

    @Test
    void given_categoryApparel_when_getProductsByCategory_then_returnsApparelProducts() {
        // Given
        String category = "apparel";
        List<Product> apparelProducts = Arrays.asList(product1, product3);
        when(productDao.findByCategory(category)).thenReturn(apparelProducts);

        // When
        List<Product> results = productService.getProductsByCategory(category);

        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> category.equals(p.getCategory())));
        verify(productDao).findByCategory(category);
    }

    @Test
    void given_categoryAccessories_when_getProductsByCategory_then_returnsAccessoriesProducts() {
        // Given
        String category = "accessories";
        List<Product> accessoriesProducts = Arrays.asList(product2);
        when(productDao.findByCategory(category)).thenReturn(accessoriesProducts);

        // When
        List<Product> results = productService.getProductsByCategory(category);

        // Then
        assertEquals(1, results.size());
        assertEquals("UC Cap", results.get(0).getName());
        verify(productDao).findByCategory(category);
    }

    @Test
    void when_getFeaturedProducts_then_returnsFeaturedProducts() {
        // Given
        List<Product> featuredProducts = Arrays.asList(product1, product3);
        when(productDao.findByFeaturedTrue()).thenReturn(featuredProducts);

        // When
        List<Product> results = productService.getFeaturedProducts();

        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(Product::isFeatured));
        verify(productDao).findByFeaturedTrue();
    }
}