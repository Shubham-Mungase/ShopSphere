package com.shopsphere.order.domain.client;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopsphere.order.domain.config.FeignAuthInterceptor;
import com.shopsphere.order.domain.entity.OrderProductSnapshot;
import com.shopsphere.order.domain.exception.ServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "product-service", configuration = FeignAuthInterceptor.class,path="/api/product")
public interface ProductServiceClient {

    Logger log = LoggerFactory.getLogger(ProductServiceClient.class);

    @GetMapping("/snapshot/{productId}")
    @CircuitBreaker(name = "productService", fallbackMethod = "fallBackProductSnapshot")
    OrderProductSnapshot getProductSnapshot(@PathVariable UUID productId);

    default OrderProductSnapshot fallBackProductSnapshot(UUID productId, Throwable t) {
        log.error("Failed to fetch product snapshot for productId={}. Reason: {}", productId, t.getMessage(), t);
        throw new ServiceUnavailableException(
                "Product-Service",
                "Cannot create order. Product service unavailable for productId=" + productId,
                t
        );
    }
}