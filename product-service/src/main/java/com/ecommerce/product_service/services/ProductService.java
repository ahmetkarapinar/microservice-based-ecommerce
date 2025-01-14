package com.ecommerce.product_service.services;

import com.ecommerce.product_service.exceptions.InvalidProductException;
import com.ecommerce.product_service.exceptions.ProductNotFoundException;
import com.ecommerce.product_service.entities.ProductEntity;
import com.ecommerce.product_service.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Service
@EnableCaching
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Add Product
    public ProductEntity addProduct(@Valid ProductEntity product) {
        return productRepository.save(product);
    }

    // Get Products by Category
    public List<ProductEntity> getProductsByCategory(String category) {
        List<ProductEntity> products = productRepository.findByCategory(category);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found in category: " + category);
        }
        return products;
    }

    // Get Product by ID
    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }
    // Get Product Price by ID
    public BigDecimal getProductPrice(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
        return product.getPrice();
    }

    // Update Product Price
    @CachePut(value = "productPrices", key = "#productId")
    public Double updateProductPrice(Long productId, Double newPrice) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        if (newPrice == null || newPrice <= 0) {
            throw new RuntimeException("Invalid price: Price must be greater than 0");
        }

        product.setPrice(BigDecimal.valueOf(newPrice));
        productRepository.save(product);
        return newPrice;
    }

    private Long getAuthUserId(){
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("UserId: "+ userId);
        return userId;
    }

    private Collection<? extends GrantedAuthority> getAuthUserRole(){
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        System.out.println("User Role: "+ authorities);
        return authorities;
    }
    // Get All Products
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    // Check if sufficient stock is available for a product
    public boolean checkStockAvailability(Long productId, Integer requiredQuantity) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        return product.getQuantity() >= requiredQuantity;
    }

    // Decrease stock for a product
    public void decreaseStock(Long productId, Integer quantityToDeduct) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        if (product.getQuantity() < quantityToDeduct) {
            throw new RuntimeException("Insufficient stock for product ID: " + productId);
        }

        product.setQuantity(product.getQuantity() - quantityToDeduct);
        productRepository.save(product);
    }
}
