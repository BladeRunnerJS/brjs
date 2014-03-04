package org.bladerunnerjs.model.exception;

public class InvalidRequirePathException extends RequirePathException {
	private static final long serialVersionUID = 1L;
	private String message;
	
	public InvalidRequirePathException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
