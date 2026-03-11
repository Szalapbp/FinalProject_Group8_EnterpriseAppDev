package com.bearcat.store_api.dao.stub;

import com.bearcat.store_api.dao.ProductDao;
import com.bearcat.store_api.entities.Product;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Repository
public class ProductDaoStub implements ProductDao{
    private final Map<Long, Product> store = new HashMap<>();
    private long nextId = 1;

    public ProductDaoStub() {
        Product p1 = new Product();
        p1.setId(nextId++);
        p1.setName("Bearcats Hoodie");
        p1.setPrice(new BigDecimal("49.99"));
        p1.setCategory("apparel");
        p1.setFeatured(true);
        store.put(p1.getId(), p1);

        Product p2 = new Product();
        p2.setId(nextId++);
        p2.setName("UC Cap");
        p2.setPrice(new BigDecimal("24.99"));
        p2.setCategory("accessories");
        p2.setFeatured(false);
        store.put(p2.getId(), p2);

        Product p3 = new Product();
        p3.setId(nextId++);
        p3.setName("Bearcats T-Shirt");
        p3.setPrice(new BigDecimal("29.99"));
        p3.setCategory("apparel");
        p3.setFeatured(true);
        store.put(p3.getId(), p3);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return store.values().stream()
                .filter(p -> category.equals(p.getCategory()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByFeaturedTrue() {
        return store.values().stream()
                .filter(Product::isFeatured)
                .collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(nextId++);
        }
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
