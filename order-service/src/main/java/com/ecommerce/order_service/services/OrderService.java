package com.ecommerce.order_service.services;

import com.ecommerce.order_service.entities.Cart;
import com.ecommerce.order_service.entities.CartItem;
import com.ecommerce.order_service.entities.OrderStatus;
import com.ecommerce.order_service.entities.Order;
import com.ecommerce.order_service.proxy.ProductServiceProxy;
import com.ecommerce.order_service.repositories.CartRepository;
import com.ecommerce.order_service.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@EnableCaching
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    @Autowired
    private ProductServiceProxy productServiceProxy;

    public OrderService(CartRepository cartRepository, OrderRepository orderRepository, ProductServiceProxy productServiceProxy) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.productServiceProxy =  productServiceProxy;
    }

    // Get Cart for a User
    public Cart getCartByUserId() {
        Long userId = getAuthUserId();
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user ID: " + userId));
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

    // Get Order by ID
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    // Update Order Status
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order proceedToPayment(String authToken) {

        Long userId = getAuthUserId();
        // Retrieve the user's car
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        // Calculate total price
        Double totalPrice = cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * getProductPrice(authToken, item.getProductId())) // Assume `getProductPrice` fetches price
                .sum();
        System.out.println("TOTAL PRICE: "+totalPrice);


         //Check stock availability for all items
        for (CartItem item : cart.getItems()) {
            boolean isStockAvailable = productServiceProxy.checkStockAvailability(authToken, item.getProductId(), item.getQuantity());
            System.out.println("ItemId:"+item.getProductId() +" stock: "+isStockAvailable);
            if (!isStockAvailable) {
                throw new RuntimeException("Insufficient stock for product ID: " + item.getProductId());
            }
        }
        // Process payment (assume payment success as placeholder)
//        boolean paymentSuccess = processPayment(userId, totalPrice);
//        if (!paymentSuccess) {
//            throw new RuntimeException("Payment processing failed");
//        }
//
//        // Decrease stock count for purchased items
//        for (CartItem item : cart.getItems()) {
//            decreaseStock(item.getProductId(), item.getQuantity());
//        }

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


    @Cacheable(value = "productPrices", key = "#productId")
    public Double getProductPrice(String authToken, Long productId) {
        Double price = productServiceProxy.getProductPrice(authToken, productId).doubleValue();
        return price;
    }

    private Long getAuthUserId(){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("UserId: "+ userId);
        return userId;
    }
}
