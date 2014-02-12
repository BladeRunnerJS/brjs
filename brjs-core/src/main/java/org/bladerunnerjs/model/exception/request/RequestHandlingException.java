package org.bladerunnerjs.model.exception.request;

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
