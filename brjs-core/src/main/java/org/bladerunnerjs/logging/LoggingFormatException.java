package org.bladerunnerjs.logging;

import java.util.IllegalFormatException;


@SuppressWarnings("serial")
public class LoggingFormatException extends Exception
{

	public LoggingFormatException(IllegalFormatException ex)
	{
		super(ex);
	}
}
