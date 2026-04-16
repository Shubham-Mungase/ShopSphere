package com.shopsphere.order.domain.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.order.domain.dto.ApiResponse;
import com.shopsphere.order.domain.dto.CreateOrderRequestDto;
import com.shopsphere.order.domain.dto.OrderResponseDto;
import com.shopsphere.order.domain.enums.OrderStatus;
import com.shopsphere.order.domain.filter.UserContext;
import com.shopsphere.order.domain.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    //  Get user
    private UserContext getUser() {
        return (UserContext) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails();
    }

    //  CREATE ORDER
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @RequestBody CreateOrderRequestDto request) {

        UserContext user = getUser();

        OrderResponseDto response =
                orderService.createOrder(user.getUserId(), request);

        return ResponseEntity.status(201)
                .body(new ApiResponse<>(true, "Order created successfully", response));
    }

    //  GET ORDER BY ID
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrderById(
            @PathVariable UUID orderId) {

        UserContext user = getUser();

        OrderResponseDto order =
                orderService.getOrderById(orderId, user.getUserId(), user.getRole());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Order fetched successfully", order)
        );
    }

    //  GET ORDERS
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getMyOrders() {

        UserContext user = getUser();

        // ADMIN → all
        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "All orders fetched",
                            orderService.getAllOrders())
            );
        }

        // USER → own
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User orders fetched",
                        orderService.getOrdersForCurrentUser(user.getUserId()))
        );
    }

    // UPDATE STATUS
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam OrderStatus status) {

        orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Order status updated", null)
        );
    }
}