package com.shopsphere.order.domain.client;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopsphere.order.domain.config.FeignAuthInterceptor;
import com.shopsphere.order.domain.entity.OrderProductSnapshot;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name="Product-Service", url = "http://localhost:8082",configuration = FeignAuthInterceptor.class)
public interface ProductServiceClient {
	
	@GetMapping("/snapshot/{productId}")
	@CircuitBreaker(name = "productService",fallbackMethod = "fallBackProductSnapshot")
	OrderProductSnapshot getProductSnapshot(@PathVariable UUID productId);

	
	default OrderProductSnapshot fallBackProductSnapshot(UUID productId, Throwable throwable) {
	    System.out.println("Product service is down for productId: " + productId);
	    throwable.printStackTrace();

	    OrderProductSnapshot empty = new OrderProductSnapshot();
	    empty.setDiscount(BigDecimal.ZERO);
	    empty.setFinalPrice(BigDecimal.ZERO);
	    empty.setProductId(productId);
	    empty.setImageUrl("Image not found");
	    empty.setPrice(BigDecimal.ZERO);
	    empty.setProductName("Unavailable Product");
	    empty.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000000")); // safer default
	    
	    return empty;
	}

}
