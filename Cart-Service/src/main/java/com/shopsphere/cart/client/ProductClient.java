package com.shopsphere.cart.client;


import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopsphere.cart.config.FeignConfig;
import com.shopsphere.cart.dto.client.ApiResponse;
import com.shopsphere.cart.dto.client.ProductResponse;

@FeignClient(
        name = "product-service",
         fallbackFactory = ProductClientFallbackFactory.class
         ,configuration = FeignConfig.class
)
public interface ProductClient {

	@GetMapping("/api/product/{productId}")
	ApiResponse<ProductResponse> getProduct(@PathVariable UUID productId);}