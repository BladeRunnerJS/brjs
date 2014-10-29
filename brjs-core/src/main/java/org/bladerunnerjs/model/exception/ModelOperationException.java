package org.bladerunnerjs.model.exception;

import org.bladerunnerjs.model.UrlContentAccessor;

/**
 * Thrown when the precise cause of the failure could not be pinpointed, and MalformedRequestException, ResourceNotFoundException and ContentProcessingException
 * have been excluded. It is an exception that we do not have a nicely specified message for. <code>ModelOperationException</code> is used primarily
 * when handling logical requests by the application request handler 
 * {@link org.bladerunnerjs.model.App#handleLogicalRequest(String, UrlContentAccessor) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, ModelOperationException}.
*/

public class ModelOperationException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;
	
	public ModelOperationException(Exception e) {
		super(e);
		message = e.getMessage();
	}

	public ModelOperationException(String message) {
		super(message);
		this.message = message;
	}
	
	public ModelOperationException(String message, Exception e) {
		super(message, e);
		this.message = message + "; " + e.getMessage();
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
