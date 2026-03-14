package com.bearcat.store_api.dao;

import com.bearcat.store_api.entities.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductDao {
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    List<Product> findByFeaturedTrue();
    Product save(Product product);
    void deleteById(Long id);
}
