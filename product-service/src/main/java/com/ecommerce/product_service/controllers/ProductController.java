package com.ecommerce.product_service.controllers;

import com.ecommerce.product_service.entities.ProductEntity;
import com.ecommerce.product_service.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Add Product (Admin Only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Restrict this endpoint to admins
    public ResponseEntity<ProductEntity> addProduct(@Valid @RequestBody ProductEntity product) {
        ProductEntity savedProduct = productService.addProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    // Get Product by Category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductEntity>> getProductsByCategory(@PathVariable String category) {
        List<ProductEntity> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    // Get Product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable Long id) {
        ProductEntity product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // Get Product Price
    @GetMapping("/{id}/price")
    public ResponseEntity<BigDecimal> getProductPrice(@PathVariable Long id) {
        BigDecimal price = productService.getProductPrice(id);
        return ResponseEntity.ok(price);
    }

    // Update Product Price
    @PreAuthorize("hasRole('ADMIN')") // Restrict this endpoint to admins
    @PutMapping("/{productId}/price")
    public ResponseEntity<Double> updateProductPrice(
            @PathVariable Long productId,
            @RequestParam  Double price) {
        Double newPrice = productService.updateProductPrice(productId, price);
        return ResponseEntity.ok(newPrice);
    }

    // Get All Products
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