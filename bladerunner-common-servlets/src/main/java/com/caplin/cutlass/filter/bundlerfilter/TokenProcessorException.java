package com.caplin.cutlass.filter.bundlerfilter;

public class TokenProcessorException extends Exception
{
	private static final long serialVersionUID = -2419303478321778000L;

	public TokenProcessorException(String message)
	{
		super(message);
	}
	
	public TokenProcessorException(Throwable t)
	{
		super(t);
	}
	
	public TokenProcessorException(String message, Throwable t)
	{
		super(message, t);
	}
}
