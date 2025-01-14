package com.ecommerce.product_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Product name is required.")
    @Size(max = 100, message = "Product name must not exceed 100 characters.")
    private String name;

    @Column(length = 100)
    @Size(max = 255, message = "Description must not exceed 255 characters.")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.")
    private BigDecimal price;

    @Column(nullable = false)
    @NotNull(message = "Quantity is required.")
    @Min(value = 0, message = "Quantity cannot be negative.")
    private Integer quantity; // Stock quantity

    @Column(nullable = false)
    @NotBlank(message = "Category is required.")
    private String category;

    @Column(nullable = false)
    private boolean isAvailable;

    public ProductEntity(String name, String description, BigDecimal price, Integer quantity, String category, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.isAvailable = isAvailable;
    }

    public ProductEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
