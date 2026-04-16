package com.shopsphere.auth.exception;

public class ErrorResponse {

    private boolean success;
    private String message;
    private String errorCode;
    private long timestamp;

    public ErrorResponse(String message, String errorCode) {
        this.success = false;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }
}
