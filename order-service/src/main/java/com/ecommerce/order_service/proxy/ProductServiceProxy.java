package com.ecommerce.order_service.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name="PRODUCT-SERVICE") //Load balancing
public interface ProductServiceProxy {

    @GetMapping("api/products/{productId}/stock/{requiredQuantity}")
    public boolean checkStockAvailability(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @PathVariable Long productId,
            @PathVariable Integer requiredQuantity);

    @GetMapping("api/products/{productId}/price")
    public BigDecimal getProductPrice(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @PathVariable Long productId);

    @PutMapping("api/products/{productId}/decrease-stock/{quantityToDeduct}")
    public ResponseEntity<Void> decreaseStock(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @PathVariable Long productId,
            @PathVariable Integer quantityToDeduct);
}
