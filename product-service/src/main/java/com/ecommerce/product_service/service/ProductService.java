package com.ecommerce.product_service.service;

import com.ecommerce.product_service.exceptions.InvalidProductException;
import com.ecommerce.product_service.exceptions.ProductNotFoundException;
import com.ecommerce.product_service.model.ProductEntity;
import com.ecommerce.product_service.repository.ProductRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Add Product
    public ProductEntity addProduct(ProductEntity product) {
        validateProduct(product); // Validate input
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

    // Validate Product Input
    private void validateProduct(ProductEntity product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new InvalidProductException("Product name cannot be empty");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductException("Product price must be greater than zero");
        }
        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new InvalidProductException("Product quantity cannot be negative");
        }
        if (product.getCategory() == null || product.getCategory().isEmpty()) {
            throw new InvalidProductException("Product category cannot be empty");
        }
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
}
