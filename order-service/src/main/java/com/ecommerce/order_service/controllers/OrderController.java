package com.ecommerce.order_service.controllers;

import com.ecommerce.order_service.entities.Cart;
import com.ecommerce.order_service.entities.Order;
import com.ecommerce.order_service.entities.OrderStatus;
import com.ecommerce.order_service.entities.ProductDto;
import com.ecommerce.order_service.proxy.ProductServiceProxy;
import com.ecommerce.order_service.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    private ProductServiceProxy productServiceProxy;

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
    // Get Order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // Update Order Status
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    // Proceed to Payment
    @PostMapping("/payment")
    public ResponseEntity<Order> proceedToPayment(
            @RequestHeader("Authorization") String authToken){

        Order order = orderService.proceedToPayment(authToken);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{productId}/stock/{requiredQuantity}")
    public boolean checkStock(
            @RequestHeader("Authorization") String authToken,
            @PathVariable Long productId,
            @PathVariable Integer requiredQuantity) {
        return productServiceProxy.checkStockAvailability(authToken,productId, requiredQuantity);
    }
}

