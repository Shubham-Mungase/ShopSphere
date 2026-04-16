package com.shopsphere.payment.exception;

public class PaymentEventCreationException extends RuntimeException {
    public PaymentEventCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}