package org.bladerunnerjs.api.model.exception;

/**
 * Thrown when the specified directory is not a valid SDK.
*/

public class InvalidSdkDirectoryException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidSdkDirectoryException(String message) {
		super(message);
	}	
}