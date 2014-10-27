package org.bladerunnerjs.model.exception.command;

public class CommandOperationException extends Exception
{
	private static final long serialVersionUID = 1L;
	private String message;
	
	public CommandOperationException(String msg)
	{
		super(msg);
		this.message = msg;
	}

	public CommandOperationException(Throwable cause)
	{
		super(cause);
		this.message = cause.getMessage();
	}

	public CommandOperationException(String msg, Throwable cause)
	{
		super(msg,cause);
		this.message = msg + "; " + cause.getMessage();
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
