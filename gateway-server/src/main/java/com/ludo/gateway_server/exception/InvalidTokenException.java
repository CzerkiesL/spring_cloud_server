package com.ludo.gateway_server.exception;

public class InvalidTokenException extends RuntimeException {

	private static final long serialVersionUID = -8997724521084555400L;

	public InvalidTokenException(String message) {
		super(message);
	}
}
