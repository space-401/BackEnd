package com.app.kkiri.global.exception;

public class DeleteFailureException extends RuntimeException {

	public final int statusCode;
	private final String message;

	public DeleteFailureException(final String message, int statusCode) {
		this.statusCode = statusCode;
		this.message = message;
	}
}
