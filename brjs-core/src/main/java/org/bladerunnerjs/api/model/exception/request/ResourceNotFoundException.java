package org.bladerunnerjs.api.model.exception.request;

/**
 * Thrown when the specified resource could not be located. 
*/

public class ResourceNotFoundException extends RequestHandlingException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException()
	{
		super("The specified resource could not be located. Please check that the requested resource exists on disk.");
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
