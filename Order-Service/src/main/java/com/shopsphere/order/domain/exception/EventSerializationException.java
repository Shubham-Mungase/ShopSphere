package com.shopsphere.order.domain.exception;


import org.springframework.http.HttpStatus;

public class EventSerializationException extends ApiException {
    public EventSerializationException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
        initCause(cause);
    }
}