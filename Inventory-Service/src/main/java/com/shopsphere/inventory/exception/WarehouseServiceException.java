package com.shopsphere.inventory.exception;

public class WarehouseServiceException extends RuntimeException {
    public WarehouseServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}