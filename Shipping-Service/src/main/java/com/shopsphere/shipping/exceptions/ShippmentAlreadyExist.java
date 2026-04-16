package com.shopsphere.shipping.exceptions;

public class ShippmentAlreadyExist extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShippmentAlreadyExist(String message) {
		super(message);
	}

}
