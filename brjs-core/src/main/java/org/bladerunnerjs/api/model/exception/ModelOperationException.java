package org.bladerunnerjs.api.model.exception;

import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.AppRequestHandler;

/**
 * Thrown when the precise cause of the failure could not be pinpointed, and MalformedRequestException, ResourceNotFoundException and ContentProcessingException
 * have been excluded. It is an exception that we do not have a nicely specified message for. <code>ModelOperationException</code> is used primarily
 * when handling logical requests by the application request handler 
 * {@link AppRequestHandler#handleLogicalRequest(String, UrlContentAccessor)}.
 * throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, ModelOperationException}.
*/

public class ModelOperationException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ModelOperationException(Exception e) {
		super(e);
	}

	public ModelOperationException(String message) {
		super(message);
	}
	
	public ModelOperationException(String message, Exception e) {
		super(message + "; " + e.getMessage(), e);
	}
}
