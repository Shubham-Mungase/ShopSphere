package com.shopsphere.cart.client;


import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopsphere.cart.dto.client.ProductResponse;

@FeignClient(
        name = "Product-Service",
        url = "http://localhost:8082",
        fallbackFactory = ProductClientFallbackFactory.class
)
public interface ProductClient {

    @GetMapping("/product/{productId}")
    ProductResponse getProduct(@PathVariable UUID productId);
}