package org.bladerunnerjs.utility;


public class NoTagHandlerFoundException extends Exception
{

	private static final long serialVersionUID = 1L;

	public NoTagHandlerFoundException(String tagName)
	{
		super("No TagHandlerPlugin found for tag name '"+tagName+"'.");
	}
	
}
