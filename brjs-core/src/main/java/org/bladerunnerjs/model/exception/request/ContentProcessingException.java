package org.bladerunnerjs.model.exception.request;




public class ContentProcessingException extends RequestHandlingException
{
	
	private static final long serialVersionUID = 7697459338111614915L;
	
	public ContentProcessingException(Throwable cause)
	{
		super(cause);
	}
	
	public ContentProcessingException(String message)
	{
		super(message);
	}
	
	public ContentProcessingException(Throwable cause, String message)
	{
		super(message, cause);
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
