package com.shopsphere.payment.exception;

public class WebhookPayloadException extends RuntimeException {
    public WebhookPayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}

