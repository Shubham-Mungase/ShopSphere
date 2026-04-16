package com.shopsphere.inventory.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.shopsphere.inventory.dto.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ===================== INVENTORY =====================

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInventoryNotFound(InventoryNotFoundException ex) {
        log.error("Inventory error: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()),
                HttpStatus.NOT_FOUND
        );
    }
    // 🔐 Handle Access Denied (@PreAuthorize)
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AuthorizationDeniedException ex) {

        ApiResponse<Object> response = new ApiResponse<>();
        response.setMessage("Access Denied: You are not authorized to perform this action");
        response.setSuccess(false);
        response.setData(null);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ===================== RESERVATION =====================

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReservationNotFound(ReservationNotFoundException ex) {
        log.error("Reservation error: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()),
                HttpStatus.NOT_FOUND
        );
    }

    // ===================== STOCK =====================

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        log.error("Stock error: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(StockMismatchException.class)
    public ResponseEntity<ErrorResponse> handleStockMismatch(StockMismatchException ex) {
        log.error("Stock mismatch: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value(), LocalDateTime.now()),
                HttpStatus.CONFLICT
        );
    }

    // ===================== WAREHOUSE =====================

    @ExceptionHandler(WarehouseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWarehouseNotFound(WarehouseNotFoundException ex) {
        log.error("Warehouse error: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStock(ProductOutOfStockException ex) {
        log.error("Out of stock: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    // ===================== MOVEMENT =====================

    @ExceptionHandler(StockMovementException.class)
    public ResponseEntity<ErrorResponse> handleStockMovement(StockMovementException ex) {
        log.error("Stock movement error: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // ===================== EVENT =====================

    @ExceptionHandler(EventSerializationException.class)
    public ResponseEntity<ErrorResponse> handleSerialization(EventSerializationException ex) {
        log.error("Event serialization error: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(WarehouseServiceException.class)
    public ResponseEntity<ErrorResponse> handleWarehouseService(WarehouseServiceException ex) {
        log.error("Warehouse service error: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // ===================== FALLBACK =====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}