package com.shopsphere.auth.exception;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException(String msg) {
        super(msg);
    }
}
