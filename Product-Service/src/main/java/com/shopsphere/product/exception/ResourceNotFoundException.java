package com.shopsphere.product.exception;

public class ResourceNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -588884733229847001L;

	public ResourceNotFoundException(String message) {
        super(message);
    }
}