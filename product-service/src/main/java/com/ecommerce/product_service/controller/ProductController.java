package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.model.ProductEntity;
import com.ecommerce.product_service.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 1. Add Product (Admin Only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Restrict this endpoint to admins
    public ResponseEntity<ProductEntity> addProduct(@RequestBody ProductEntity product) {
        ProductEntity savedProduct = productService.addProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    // 2. Get Product by Category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductEntity>> getProductsByCategory(@PathVariable String category) {
        List<ProductEntity> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    // 3. Get Product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable Long id) {
        ProductEntity product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // 4. Get All Products
    @GetMapping
    public ResponseEntity<List<ProductEntity>> getAllProducts() {
        List<ProductEntity> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // Check stock availability
    @GetMapping("/{productId}/stock/{requiredQuantity}")
    public ResponseEntity<Boolean> checkStockAvailability(
            @PathVariable Long productId,
            @PathVariable Integer requiredQuantity) {
        boolean isAvailable = productService.checkStockAvailability(productId, requiredQuantity);
        return ResponseEntity.ok(isAvailable);
    }

    // Decrease stock
    @PutMapping("/{productId}/decrease-stock/{quantityToDeduct}")
    public ResponseEntity<Void> decreaseStock(
            @PathVariable Long productId,
            @PathVariable Integer quantityToDeduct) {
        productService.decreaseStock(productId, quantityToDeduct);
        return ResponseEntity.noContent().build();
    }

}