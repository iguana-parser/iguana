package org.jgll.exception;


@SuppressWarnings("serial")
public class ValidationFailedException extends RuntimeException {
	
	private final String message;

	public ValidationFailedException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return message;
	}
	
}
