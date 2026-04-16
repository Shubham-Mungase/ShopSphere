package com.shopsphere.payment.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    
    // 🔐 Handle Access Denied (@PreAuthorize)
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AuthorizationDeniedException ex,HttpServletRequest request) {

    	 ApiErrorResponse response = new ApiErrorResponse(
                 HttpStatus.UNAUTHORIZED.value(),
                 HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                 ex.getMessage(),
                 request.getRequestURI()
         );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    // 🔹 Webhook signature verification failure
    @ExceptionHandler(WebhookVerificationException.class)
    public ResponseEntity<ApiErrorResponse> handleWebhookVerification(WebhookVerificationException ex,
                                                                      HttpServletRequest request) {
        log.warn("Webhook verification failed: {}", ex.getMessage(), ex);
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 🔹 Webhook payload issues
    @ExceptionHandler(WebhookPayloadException.class)
    public ResponseEntity<ApiErrorResponse> handleWebhookPayload(WebhookPayloadException ex,
                                                                 HttpServletRequest request) {
        log.error("Webhook payload error: {}", ex.getMessage(), ex);
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 🔹 Payment not found
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentNotFound(PaymentNotFoundException ex,
                                                                  HttpServletRequest request) {
        log.error("Payment not found: {}", ex.getMessage(), ex);
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 🔹 Payment ownership violation
    @ExceptionHandler(PaymentOwnershipException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentOwnership(PaymentOwnershipException ex,
                                                                   HttpServletRequest request) {
        log.error("Payment ownership violation: {}", ex.getMessage(), ex);
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 🔹 Payment gateway / Razorpay issues
    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentGateway(PaymentGatewayException ex,
                                                                 HttpServletRequest request) {
        log.error("Payment gateway error: {}", ex.getMessage(), ex);
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_GATEWAY.value(),
                HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
    }

    // 🔹 Payment outbox / event creation issues
    @ExceptionHandler(PaymentEventCreationException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentEvent(PaymentEventCreationException ex,
                                                               HttpServletRequest request) {
        log.error("Payment outbox event creation failed: {}", ex.getMessage(), ex);
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 🔹 Fallback for any other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex,
                                                                   HttpServletRequest request) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Unexpected error occurred",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}