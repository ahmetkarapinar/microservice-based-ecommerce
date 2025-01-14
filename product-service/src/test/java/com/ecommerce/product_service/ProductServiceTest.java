package com.ecommerce.product_service;

import com.ecommerce.product_service.entities.ProductEntity;
import com.ecommerce.product_service.repositories.ProductRepository;
import com.ecommerce.product_service.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProduct_Success() {
        // Arrange
        ProductEntity product = new ProductEntity("Laptop", "High-end laptop", new BigDecimal("1200.00"), 10, "Electronics", true);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(product);

        // Act
        ProductEntity result = productService.addProduct(product);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals("High-end laptop", result.getDescription());
        verify(productRepository).save(product);
    }

    @Test
    void getProductById_Success() {
        // Arrange
        ProductEntity product = new ProductEntity("Laptop", "High-end laptop", new BigDecimal("1200.00"), 10, "Electronics", true);
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductEntity result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_NotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> productService.getProductById(1L));
        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductsByCategory_Success() {
        // Arrange
        ProductEntity product1 = new ProductEntity("Laptop", "High-end laptop", new BigDecimal("1200.00"), 10, "Electronics", true);
        ProductEntity product2 = new ProductEntity("Headphones", "Noise-cancelling headphones", new BigDecimal("200.00"), 20, "Electronics", true);
        List<ProductEntity> products = Arrays.asList(product1, product2);

        when(productRepository.findByCategory("Electronics")).thenReturn(products);

        // Act
        List<ProductEntity> result = productService.getProductsByCategory("Electronics");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository).findByCategory("Electronics");
    }

}
