package com.bearcat.store_api.service;

import com.bearcat.store_api.entities.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    List<Product> getProductsByCategory(String category);
    List<Product> getFeaturedProducts();
}
