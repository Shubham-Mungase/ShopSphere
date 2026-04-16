package com.shopsphere.inventory.exception;

public class StockMismatchException extends RuntimeException {
    public StockMismatchException(String message) {
        super(message);
    }
}