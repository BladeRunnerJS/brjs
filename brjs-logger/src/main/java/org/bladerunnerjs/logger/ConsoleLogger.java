package org.bladerunnerjs.logger;

import org.bladerunnerjs.logger.LogLevel;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

public class ConsoleLogger extends MarkerIgnoringBase
{
	private static final long serialVersionUID = 1L;
	
	private final String name;
	private final RootConsoleLogger rootLogger;
	
	public ConsoleLogger(String name, RootConsoleLogger rootLogger)
	{
		this.name = name;
		this.rootLogger = rootLogger;
	}
	
	private void formatAndLog(LogLevel logLevel, String format, Object arg)
	{
		FormattingTuple formattingTuple = MessageFormatter.format(format, arg);
		rootLogger.log(logLevel, name, formattingTuple.getMessage(), formattingTuple.getThrowable());
	}
	
	private void formatAndLog(LogLevel logLevel, String format, Object arg1, Object arg2)
	{
		FormattingTuple formattingTuple = MessageFormatter.format(format, arg1, arg2);
		rootLogger.log(logLevel, name, formattingTuple.getMessage(), formattingTuple.getThrowable());
	}
	
	private void formatAndLog(LogLevel logLevel, String format, Object[] argArray)
	{
		FormattingTuple formattingTuple = MessageFormatter.arrayFormat(format, argArray);
		rootLogger.log(logLevel, name, formattingTuple.getMessage(), formattingTuple.getThrowable());
	}
	
	@Override
	public boolean isTraceEnabled()
	{
		return false;
	}
	
	@Override
	public void trace(String msg)
	{
		rootLogger.log(LogLevel.DEBUG, name, msg, null);
	}
	
	@Override
	public void trace(String format, Object arg)
	{
		formatAndLog(LogLevel.DEBUG, format, arg);
	}
	
	@Override
	public void trace(String format, Object arg1, Object arg2)
	{
		formatAndLog(LogLevel.DEBUG, format, arg1, arg2);
	}
	
	@Override
	public void trace(String format, Object[] argArray)
	{
		formatAndLog(LogLevel.DEBUG, format, argArray);
	}
	
	@Override
	public void trace(String msg, Throwable t)
	{
		rootLogger.log(LogLevel.DEBUG, name, msg, t);
	}
	
	@Override
	public boolean isDebugEnabled()
	{
		return (rootLogger.getLogLevel().ordinal() <= LogLevel.DEBUG.ordinal());
	}
	
	@Override
	public void debug(String msg)
	{
		rootLogger.log(LogLevel.DEBUG, name, msg, null);
	}
	
	@Override
	public void debug(String format, Object arg)
	{
		formatAndLog(LogLevel.DEBUG, format, arg);
	}
	
	@Override
	public void debug(String format, Object arg1, Object arg2)
	{
		formatAndLog(LogLevel.DEBUG, format, arg1, arg2);
	}
	
	@Override
	public void debug(String format, Object[] argArray)
	{
		formatAndLog(LogLevel.DEBUG, format, argArray);
	}
	
	@Override
	public void debug(String msg, Throwable t)
	{
		rootLogger.log(LogLevel.DEBUG, name, msg, t);
	}
	
	@Override
	public boolean isInfoEnabled()
	{
		return (rootLogger.getLogLevel().ordinal() <= LogLevel.INFO.ordinal());
	}
	
	@Override
	public void info(String msg)
	{
		rootLogger.log(LogLevel.INFO, name, msg, null);
	}
	
	@Override
	public void info(String format, Object arg)
	{
		formatAndLog(LogLevel.INFO, format, arg);
	}
	
	@Override
	public void info(String format, Object arg1, Object arg2)
	{
		formatAndLog(LogLevel.INFO, format, arg1, arg2);
	}
	
	@Override
	public void info(String format, Object[] argArray)
	{
		formatAndLog(LogLevel.INFO, format, argArray);
	}
	
	@Override
	public void info(String msg, Throwable t)
	{
		rootLogger.log(LogLevel.INFO, name, msg, t);
	}
	
	@Override
	public boolean isWarnEnabled()
	{
		return (rootLogger.getLogLevel().ordinal() <= LogLevel.WARN.ordinal());
	}
	
	@Override
	public void warn(String msg)
	{
		rootLogger.log(LogLevel.WARN, name, msg, null);
	}
	
	@Override
	public void warn(String format, Object arg)
	{
		formatAndLog(LogLevel.WARN, format, arg);
	}
	
	@Override
	public void warn(String format, Object arg1, Object arg2)
	{
		formatAndLog(LogLevel.WARN, format, arg1, arg2);
	}
	
	@Override
	public void warn(String format, Object[] argArray)
	{
		formatAndLog(LogLevel.WARN, format, argArray);
	}
	
	@Override
	public void warn(String msg, Throwable t)
	{
		rootLogger.log(LogLevel.WARN, name, msg, t);
	}
	
	@Override
	public boolean isErrorEnabled()
	{
		return (rootLogger.getLogLevel().ordinal() <= LogLevel.ERROR.ordinal());
	}
	
	@Override
	public void error(String msg)
	{
		rootLogger.log(LogLevel.ERROR, name, msg, null);
	}
	
	@Override
	public void error(String format, Object arg)
	{
		formatAndLog(LogLevel.ERROR, format, arg);
	}
	
	@Override
	public void error(String format, Object arg1, Object arg2)
	{
		formatAndLog(LogLevel.ERROR, format, arg1, arg2);
	}
	
	@Override
	public void error(String format, Object[] argArray)
	{
		formatAndLog(LogLevel.ERROR, format, argArray);
	}
	
	@Override
	public void error(String msg, Throwable t)
	{
		rootLogger.log(LogLevel.ERROR, name, msg, t);
	}
}
