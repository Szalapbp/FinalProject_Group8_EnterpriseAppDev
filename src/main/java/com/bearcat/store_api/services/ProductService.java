package com.bearcat.store_api.services;
import com.bearcat.store_api.entities.Product;
import com.bearcat.store_api.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public List<Product> getFeaturedProducts(){
        return productRepository.findByFeaturedTrue();
    }

    public List<Product> getProductsByCategory(String category){
        return productRepository.findByCategory(category);
    }
}



