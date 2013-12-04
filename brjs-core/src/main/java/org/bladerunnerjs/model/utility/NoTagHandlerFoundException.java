package org.bladerunnerjs.model.utility;


public class NoTagHandlerFoundException extends Exception
{

	private static final long serialVersionUID = -2248045619960247541L;

	public NoTagHandlerFoundException(String tagName)
	{
		super("No TagHandlerPlugin found for tag name '"+tagName+"'.");
	}
	
}
