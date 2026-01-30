package com.shopsphere.order.domain.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.order.domain.dto.CreateOrderRequestDto;
import com.shopsphere.order.domain.dto.OrderResponseDto;
import com.shopsphere.order.domain.enums.OrderStatus;
import com.shopsphere.order.domain.service.OrderService;
import com.shopsphere.order.domain.utils.SecurityUtils;

@RestController
@RequestMapping("/orders")
public class OrderRestController {

    private final OrderService orderService;
    
    
    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

   
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody CreateOrderRequestDto request) {

    	try {
    		 UUID userId = SecurityUtils.getCurrentUserId();

    	        OrderResponseDto response = orderService.createOrder(userId, request);

    	        return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			 return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
       
    }

   
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable UUID orderId) {

        OrderResponseDto response = orderService.getOrderById(orderId);

        return ResponseEntity.ok(response);
    }

    
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMyOrders() {

    	 UUID userId = SecurityUtils.getCurrentUserId();
        List<OrderResponseDto> orders = orderService.getOrdersForCurrentUser(userId);

        return ResponseEntity.ok(orders);
    }

    
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam OrderStatus status) {

        orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.noContent().build();
    }
}
