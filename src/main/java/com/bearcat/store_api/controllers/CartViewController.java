package com.bearcat.store_api.controllers;

import com.bearcat.store_api.entities.Cart;
import com.bearcat.store_api.entities.CartItem;
import com.bearcat.store_api.entities.Product;
import com.bearcat.store_api.entities.User;
import com.bearcat.store_api.repositories.CartItemRepository;
import com.bearcat.store_api.repositories.CartRepository;
import com.bearcat.store_api.repositories.ProductRepository;
import com.bearcat.store_api.services.CartService;
import com.bearcat.store_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CartViewController {

    private final UserService userService;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;


    @GetMapping("/cart")
    public String viewCart(Model model) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getOrCreateCart(user);

        List<CartItem> items = cart.getCartItems();
        BigDecimal totalPrice = items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalItems = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        model.addAttribute("items", items);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalItems", totalItems);

        return "cart";
    }


    @PostMapping("/cart")
    public String addItemToCart(   @RequestParam Long productId,
                                   @RequestParam String size,
                                   @RequestParam(defaultValue = "1") Integer quantity,Model model) {

        User user = userService.getCurrentUser();

        Cart cart = cartService.getOrCreateCart(user);


        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId)
                        && item.getSize().equals(size))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setSize(size);
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        List<CartItem> items = cart.getCartItems();

        BigDecimal totalPrice = items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItem::getQuantity).sum();

        model.addAttribute("items", items);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalItems", totalItems);

        return "cart";
    }

    @PutMapping("/cart/items/{id}")
    public String updateQuantity(@PathVariable Long id,
                                 @RequestParam Integer quantity) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return "redirect:/cart";
    }


    @DeleteMapping("/cart/items/{id}")
    public String removeItem(@PathVariable Long id) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        cartItemRepository.delete(item);

        return "redirect:/cart";
    }

    @DeleteMapping("/cart/clear")
    public String clearCart() {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getOrCreateCart(user);

        // Get all cart items
        List<CartItem> cartItems = cart.getCartItems();

        if (!cartItems.isEmpty()) {
            // Clear the cart's collection
            cart.getCartItems().clear();

            // Delete all cart items from database
            cartItemRepository.deleteAll(cartItems);

            // Save the updated cart
            cartRepository.save(cart);
        }

        return "redirect:/cart";
    }




}