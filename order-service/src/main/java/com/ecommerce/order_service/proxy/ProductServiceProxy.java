package com.ecommerce.order_service.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="PRODUCT-SERVICE") //Load balancing
public class ProductServiceProxy {

    @GetMapping("/products/{from}/quantity/{quatity}")
    public boolean checkStockAvailability(
            @PathVariable String from,
            @PathVariable String to);
}
