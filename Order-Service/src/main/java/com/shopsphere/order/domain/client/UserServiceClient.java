package com.shopsphere.order.domain.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopsphere.order.domain.entity.OrderAddressSnapshot;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "User-Service",path = "/api/internal/users",  url = "http://localhost:8080")
public interface UserServiceClient {
	
	@GetMapping("{userId}/addresses/{addressId}")
	@CircuitBreaker(name="userService",fallbackMethod = "fallBackAddressSnapshot")
	OrderAddressSnapshot getAddressSnapshot(@PathVariable UUID userId,@PathVariable UUID addressId);

	default OrderAddressSnapshot fallBackAddressSnapshot(UUID userId, UUID addressId, Throwable t)
	{
	    OrderAddressSnapshot empty = new OrderAddressSnapshot();
	    empty.setCity("Unknown City");
	    empty.setDefaultAddress(false);
	    empty.setFullName("Unknown Name");
	    empty.setPhone("0000000000");
	    empty.setPincode("000000");
	    empty.setState("Unknown State");
	    
	    // Optional: log the error
	    System.out.println("User service is down! Returning fallback for userId: " + userId + ", addressId: " + addressId);
	    t.printStackTrace();
	    
	    return empty;
	}

}
