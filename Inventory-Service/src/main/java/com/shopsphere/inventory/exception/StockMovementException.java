package com.shopsphere.inventory.exception;

public class StockMovementException extends RuntimeException {
    public StockMovementException(String message, Throwable cause) {
        super(message, cause);
    }
}