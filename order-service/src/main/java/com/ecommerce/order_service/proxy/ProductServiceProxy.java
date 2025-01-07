package com.ecommerce.order_service.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="PRODUCT-SERVICE") //Load balancing
public interface ProductServiceProxy {

    @GetMapping("api/products/{productId}/stock/{requiredQuantity}")
    public boolean checkStockAvailability(
            @PathVariable Long productId,
            @PathVariable Integer requiredQuantity);
}
