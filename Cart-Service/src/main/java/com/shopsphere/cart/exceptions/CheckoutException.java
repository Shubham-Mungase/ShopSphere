package com.shopsphere.cart.exceptions;

public class CheckoutException extends RuntimeException {
    public CheckoutException(String message, Throwable cause) {
        super(message, cause);
    }
}