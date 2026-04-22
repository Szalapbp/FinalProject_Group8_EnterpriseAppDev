package com.bearcat.store_api.controllers;

import com.bearcat.store_api.entities.*;
import com.bearcat.store_api.repositories.CartItemRepository;
import com.bearcat.store_api.repositories.CartRepository;
import com.bearcat.store_api.repositories.OrderRepository;
import com.bearcat.store_api.services.CartService;
import com.bearcat.store_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final UserService userService;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        User user = userService.getCurrentUser();
        Cart cart = cartService.getOrCreateCart(user);
        List<CartItem> items = cart.getCartItems();

//        if (items.isEmpty()) {
//            return "redirect:/cart";
//        }

        BigDecimal totalAmount = items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("items", items);
        model.addAttribute("totalAmount", totalAmount);

        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String shippingAddress,
                             RedirectAttributes redirectAttributes) {

        User user = userService.getCurrentUser();
        Cart cart = cartService.getOrCreateCart(user);
        List<CartItem> items = cart.getCartItems();

        if (items.isEmpty()) {
            return "redirect:/cart";
        }

        // Build order
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setOrderDate(Instant.now());

        BigDecimal totalAmount = items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);

        for (CartItem cartItem : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setSize(cartItem.getSize());
            order.addOrderItem(orderItem);
        }

        orderRepository.save(order);

        // Clear the cart
        cart.getCartItems().clear();
        cartItemRepository.deleteAll(items);
        cartRepository.save(cart);

//        redirectAttributes.addFlashAttribute("orderSuccess", true);
//        redirectAttributes.addFlashAttribute("orderId", order.getId());

//        return "redirect:/checkout/confirmation";

        return "redirect:/home";
    }


//
//    @GetMapping("/checkout/confirmation")
//    public String confirmation() {
//        return "confirmation";
//    }


}