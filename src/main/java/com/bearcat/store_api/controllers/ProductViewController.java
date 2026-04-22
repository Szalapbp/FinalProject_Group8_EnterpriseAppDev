package com.bearcat.store_api.controllers;
import com.bearcat.store_api.entities.Product;
import com.bearcat.store_api.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProductViewController {

    private final ProductService productService;

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String search,
            Model model
    ) {
        // 1. Fetch by category or all
        List<Product> products = (category != null && !category.isBlank())
                ? productService.getProductsByCategory(category)
                : productService.getAllProducts();

        // 2. Filter by search term
        if (search != null && !search.isBlank()) {
            String term = search.toLowerCase();
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(term))
                    .collect(Collectors.toList());
        }

        // 3. Sort
        if (sort != null) {
            switch (sort) {
                case "price_asc"  -> products.sort(Comparator.comparing(Product::getPrice));
                case "price_desc" -> products.sort(Comparator.comparing(Product::getPrice).reversed());
                case "name"       -> products.sort(Comparator.comparing(Product::getName));
                case "featured"   -> products.sort(Comparator.comparing(Product::isFeatured).reversed());
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("totalCount", products.size());
        model.addAttribute("selectedCategory", category != null ? category : "");
        model.addAttribute("selectedSort", sort != null ? sort : "");
        model.addAttribute("search", search != null ? search : "");

        return "products";
    }
}

