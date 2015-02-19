package org.bladerunnerjs.api.model.exception.request;

/**
 * This is the superclass for 
 * @see org.bladerunnerjs.api.model.exception.request.ContentProcessingException
 * @see org.bladerunnerjs.api.model.exception.request.MalformedRequestException
 * @see org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException
*/

public abstract class RequestHandlingException extends Exception
{

	private static final long serialVersionUID = 1L;
	
	public RequestHandlingException(String message)
	{
		super(message);
	}
	
	public RequestHandlingException(Throwable wrappedException)
	{
		super(wrappedException);
	}
	
	public RequestHandlingException(String message, Throwable wrappedException)
	{
		super(message, wrappedException);
	}	
}
