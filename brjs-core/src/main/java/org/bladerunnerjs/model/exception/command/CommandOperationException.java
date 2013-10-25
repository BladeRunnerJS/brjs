package org.bladerunnerjs.model.exception.command;

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
		super(msg,cause);
	}
}
