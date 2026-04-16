package com.shopsphere.inventory.exception;

public class InventoryProcessingException extends RuntimeException {
    public InventoryProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}