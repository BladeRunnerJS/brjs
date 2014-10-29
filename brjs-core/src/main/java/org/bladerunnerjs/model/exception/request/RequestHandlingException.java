package org.bladerunnerjs.model.exception.request;

/**
 * This is the superclass for 
 * @see org.bladerunnerjs.model.exception.request.ContentProcessingException
 * @see org.bladerunnerjs.model.exception.request.MalformedRequestException
 * @see org.bladerunnerjs.model.exception.request.ResourceNotFoundException
*/

public abstract class RequestHandlingException extends Exception
{

	private static final long serialVersionUID = 1L;
	private String message;
	
	public RequestHandlingException(String message)
	{
		super(message);
		this.message = message;
	}
	
	public RequestHandlingException(Throwable wrappedException)
	{
		super(wrappedException);
		this.message = wrappedException.getMessage();
	}
	
	public RequestHandlingException(String message, Throwable wrappedException)
	{
		super(message, wrappedException);
		this.message = message + "; " + wrappedException.getMessage();
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
}
