package com.ecommerce.order_service.controllers;

import com.ecommerce.order_service.entities.Cart;
import com.ecommerce.order_service.entities.Order;
import com.ecommerce.order_service.entities.ProductDto;
import com.ecommerce.order_service.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Add Product to Cart
    @PostMapping("/cart")
    public ResponseEntity<Cart> addProductToCart(
            @RequestBody ProductDto product) {
        Cart cart = orderService.addProductToCart(product.getProductId(), product.getQuantity());
        return ResponseEntity.ok(cart);
    }
    @GetMapping
    public String hello() {
        return "Hello World!";
    }

    // Remove Product from Cart
    @DeleteMapping("/cart")
    public ResponseEntity<Cart> removeProductFromCart(
            @RequestParam Long productId) {
        Cart cart = orderService.removeProductFromCart(productId);
        return ResponseEntity.ok(cart);
    }

    // Proceed to Payment
    @PostMapping("/payment")
    public ResponseEntity<Order> proceedToPayment(
            @RequestBody Long userId,
            @RequestBody Double paymentAmount) {
        Order order = orderService.proceedToPayment(userId, paymentAmount);
        return ResponseEntity.ok(order);
    }
}

