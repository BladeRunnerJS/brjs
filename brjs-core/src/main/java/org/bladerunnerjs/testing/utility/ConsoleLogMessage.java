package org.bladerunnerjs.testing.utility;


public class ConsoleLogMessage extends LogMessage
{

	public ConsoleLogMessage(String message, Object[] params)
	{
		super( message, params );
	}
	
	@Override
	public String toString()
	{
		return getFormattedMessage();
	}

}
