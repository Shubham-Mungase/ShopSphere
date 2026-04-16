package com.shopsphere.order.domain.client;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopsphere.order.domain.config.FeignAuthInterceptor;
import com.shopsphere.order.domain.entity.OrderAddressSnapshot;
import com.shopsphere.order.domain.exception.ServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "user-service", path = "/api/users/internal/users",configuration = FeignAuthInterceptor.class )
public interface UserServiceClient {

    Logger log = LoggerFactory.getLogger(UserServiceClient.class);

    @GetMapping("/addresses/{addressId}")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallBackAddressSnapshot")
    OrderAddressSnapshot getAddressSnapshot(
          @PathVariable UUID addressId
    );

    /**
     * CircuitBreaker fallback method
     */
    default OrderAddressSnapshot fallBackAddressSnapshot( UUID addressId, Throwable t) {
        log.error("Failed to fetch address snapshot for addressId={}. Reason: {}",
                 addressId, t.getMessage(), t);

        throw new ServiceUnavailableException(
                "User-Service",
                "Cannot fetch address snapshot for  addressId=" + addressId,
                t
        );
    }
}