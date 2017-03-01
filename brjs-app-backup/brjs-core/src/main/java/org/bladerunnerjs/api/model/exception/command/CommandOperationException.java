package org.bladerunnerjs.api.model.exception.command;

/**
 * Thrown when the command operations failed e.g. due to invalid arguments or URL, error creating directory, no tests found in specified locations,
 * non-existent test path or failing test.
*/ 

public class CommandOperationException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public CommandOperationException(String msg)
	{
		super(msg);
	}

	public CommandOperationException(Throwable cause)
	{
		super(cause);
	}

	public CommandOperationException(String msg, Throwable cause)
	{
		super(msg + "; " + cause.getMessage(), cause);
	}
}
