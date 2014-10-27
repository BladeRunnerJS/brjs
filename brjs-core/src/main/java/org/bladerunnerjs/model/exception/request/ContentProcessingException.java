package org.bladerunnerjs.model.exception.request;

/**
 * Class derived from RequestHandlingException - Exception - Throwable - Object.
 * Thrown when the request has failed, but has not been due to a malformed request or resource not found. 
*/ 

public class ContentProcessingException extends RequestHandlingException
{
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	public ContentProcessingException(Throwable cause)
	{
		super(cause);
		message = cause.getMessage();
	}
	
	public ContentProcessingException(String message)
	{
		super(message);
		this.message = message;
	}
	
	public ContentProcessingException(Throwable cause, String message)
	{
		super(message, cause);
		this.message = message + "; " + cause.getMessage();
	}
		
	@Override
	public String getMessage() {
		return message;
	}
	
	public String toString()
	{
		StringBuilder errorMessage = new StringBuilder();
		
		if(this.getCause() != null)
		{
			errorMessage.append(getMessage() + "\nCaused by: " + getCause().getMessage());
		}
		
		String message = getMessage();
		if(message != null)
		{
			errorMessage.append(message + "\n");
		}
		
		else
		{
			errorMessage.append("The stack trace for the exception is below.\n");
			StackTraceElement[] stackTrace = getStackTrace();
			for(StackTraceElement traceElement : stackTrace)
			{
				errorMessage.append("\t" + traceElement.toString() + "\n");
			}
		}
		
		return errorMessage.toString();
	}
}
