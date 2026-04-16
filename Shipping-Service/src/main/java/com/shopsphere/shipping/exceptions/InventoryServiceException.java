package com.shopsphere.shipping.exceptions;

public class InventoryServiceException extends RuntimeException {
	public InventoryServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}