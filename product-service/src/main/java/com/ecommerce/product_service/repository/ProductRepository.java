package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByCategory(String category);
}
