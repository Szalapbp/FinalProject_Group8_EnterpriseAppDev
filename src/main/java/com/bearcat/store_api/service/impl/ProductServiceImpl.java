package com.bearcat.store_api.service.impl;

import com.bearcat.store_api.dao.ProductDao;
import com.bearcat.store_api.entities.Product;
import com.bearcat.store_api.service.ProductService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{
    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productDao.findById(id);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productDao.findByCategory(category);
    }

    @Override
    public List<Product> getFeaturedProducts() {
        return productDao.findByFeaturedTrue();
    }
}
