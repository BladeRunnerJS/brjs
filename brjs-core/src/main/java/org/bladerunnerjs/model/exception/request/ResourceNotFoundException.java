package org.bladerunnerjs.model.exception.request;



public class ResourceNotFoundException extends RequestHandlingException {

	private static final long serialVersionUID = -7934305990312796421L;

	public ResourceNotFoundException()
	{
		super("The specified resource could not be located. Please check that the requested resource exists on disk.");
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
