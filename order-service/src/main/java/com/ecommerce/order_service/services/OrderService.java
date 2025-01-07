package com.ecommerce.order_service.services;

import com.ecommerce.order_service.entities.Cart;
import com.ecommerce.order_service.entities.CartItem;
import com.ecommerce.order_service.entities.Order;
import com.ecommerce.order_service.proxy.ProductServiceProxy;
import com.ecommerce.order_service.repositories.CartRepository;
import com.ecommerce.order_service.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    @Autowired
    private ProductServiceProxy productProxy;

    public OrderService(CartRepository cartRepository, OrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

// Add Product to Cart
    public Cart addProductToCart(Long productId, Integer quantity) {
        // Retrieve or create a cart for the user
        Long userId = getAuthUserId();
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return newCart;
        });

        // Check if the product already exists in the cart
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    // Create a new item if it doesn't exist
                    CartItem newItem = new CartItem();
                    newItem.setProductId(productId);
                    newItem.setQuantity(0);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        // Update the quantity of the existing/new item
        item.setQuantity(item.getQuantity() + quantity);

        // Save the updated cart
        return cartRepository.save(cart);
    }

    // Remove Product from Cart
    public Cart removeProductFromCart(Long productId) {
        Long userId = getAuthUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        return cartRepository.save(cart);
    }

    public Order proceedToPayment(Long userId, Double paymentAmount) {
        // Retrieve the user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        // Calculate total price
        Double totalPrice = cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * getProductPrice(item.getProductId())) // Assume `getProductPrice` fetches price
                .sum();

        if (!paymentAmount.equals(totalPrice)) {
            throw new RuntimeException("Payment amount does not match the total cart price");
        }

        // Check stock availability for all items
        for (CartItem item : cart.getItems()) {
            boolean isStockAvailable = checkStockAvailability(item.getProductId(), item.getQuantity());
            if (!isStockAvailable) {
                throw new RuntimeException("Insufficient stock for product ID: " + item.getProductId());
            }
        }

        // Process payment (assume payment success as placeholder)
        boolean paymentSuccess = processPayment(userId, totalPrice);
        if (!paymentSuccess) {
            throw new RuntimeException("Payment processing failed");
        }

        // Decrease stock count for purchased items
        for (CartItem item : cart.getItems()) {
            decreaseStock(item.getProductId(), item.getQuantity());
        }

        // Create Order
        Order order = new Order();
        order.setUserId(userId);
        order.setItems(new ArrayList<>(cart.getItems()));
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        // Clear the cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    // Example method to get product price (to be replaced with Product Service integration)
    private Double getProductPrice(Long productId) {
        return 100.0; // Placeholder price
    }

    private Long getAuthUserId(){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("UserId: "+ userId);
        return userId;
    }
}
