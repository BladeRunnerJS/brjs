package org.bladerunnerjs.api.model.exception;

/**
 * Thrown when an invalid location has been specified for the request.
*/

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
