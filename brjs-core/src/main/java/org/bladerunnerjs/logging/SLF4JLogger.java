package org.bladerunnerjs.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatException;

import org.bladerunnerjs.api.logging.Logger;

public class SLF4JLogger implements Logger
{
	private final org.slf4j.Logger slf4jLogger;
	private final String name;
	private LoggerTimeAccessor timeAccessor;

	public SLF4JLogger(org.slf4j.Logger slf4jLogger, String name)
	{
		this(slf4jLogger, name, null);
	}
	
	SLF4JLogger(org.slf4j.Logger slf4jLogger, String name, LoggerTimeAccessor timeAccessor)
	{
		this.slf4jLogger = slf4jLogger;
		this.name = name;
		this.timeAccessor = (timeAccessor != null) ? timeAccessor : new DefaultTimeAccessor();;
	}
	
	@Override
	public void error(String message, Object... params)
	{
		if (slf4jLogger.isErrorEnabled())
		{
			slf4jLogger.error( getFormattedString(message, params) );
		}
	}

	@Override
	public void warn(String message, Object... params)
	{
		if (slf4jLogger.isWarnEnabled())
		{
			slf4jLogger.warn( getFormattedString(message, params) );
		}
	}

	@Override
	public void info(String message, Object... params)
	{
		if (slf4jLogger.isInfoEnabled())
		{
			slf4jLogger.info( getFormattedString(message, params) );
		}
	}

	@Override
	public void debug(String message, Object... params)
	{
		if (slf4jLogger.isDebugEnabled())
		{
			slf4jLogger.debug( getFormattedString(message, params) );
		}
	}
	
	private String getFormattedString(String message, Object... params)
	{
		String timestamp = timeAccessor.getTimestamp();
		String timestampPrefix = (slf4jLogger.isDebugEnabled() && timestamp != null) ? timestamp+" - " : ""; 
		try {
			return timestampPrefix + ((params.length == 0) ? message : String.format(message, params));
		}
		catch (IllegalFormatException ex) /* IllegalFormatException is a runtime exception */
		{
			slf4jLogger.error("Attempted to log a message but received a " + ex.getClass().getCanonicalName() + ".\n"+
					"Message: " + message + "\n"+
					"Params: " + params);
			throw ex;
		}
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void println(String message, Object... params)
	{
		System.out.println( getFormattedString(message, params) );
	}

	@Override
	public void console(String message, Object... params)
	{
		println(message, params);
	}

	
	private class DefaultTimeAccessor implements LoggerTimeAccessor {
		@Override
		public String getTimestamp()
		{
			return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		}
	}
	
}
