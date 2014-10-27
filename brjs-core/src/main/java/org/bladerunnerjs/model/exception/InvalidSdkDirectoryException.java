package org.bladerunnerjs.model.exception;

public class InvalidSdkDirectoryException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;
	
	public InvalidSdkDirectoryException(String message) {
		super(message);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
}