package com.caplin.cutlass.exception;

public class NamespaceException extends Exception
{
	
	private static final long serialVersionUID = -4941774222693079191L;

	public NamespaceException(String message)
	{
		super(message);
	}
	
	public NamespaceException(String message, Exception e)
	{
		super(message, e);
	}
}
