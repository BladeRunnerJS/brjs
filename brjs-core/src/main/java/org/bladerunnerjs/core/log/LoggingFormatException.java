package org.bladerunnerjs.core.log;

import java.util.IllegalFormatException;


@SuppressWarnings("serial")
public class LoggingFormatException extends Exception
{

	public LoggingFormatException(IllegalFormatException ex)
	{
		super(ex);
	}
}
