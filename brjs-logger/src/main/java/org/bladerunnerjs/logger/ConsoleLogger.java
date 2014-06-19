package org.bladerunnerjs.logger;

import java.io.PrintStream;
import java.util.List;

import org.bladerunnerjs.logger.LogLevel;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

public class ConsoleLogger extends MarkerIgnoringBase
{
	private static final long serialVersionUID = 1L;
	
	private final String className;
	private final ConsoleLoggerStore loggerStore;
	private boolean enabled = false;
	
	public ConsoleLogger(String className, ConsoleLoggerStore loggerStore)
	{
		this.className = className;
		this.loggerStore = loggerStore;
	}
	
	public void setWhiteListedPackages(List<String> whitelistedPackages) {
		for(String whitelistedPackage : whitelistedPackages) {
			if(whitelistedPackage.equals("*") || className.startsWith(whitelistedPackage)) {
				enabled = true;
			}
		}
	}
	
	private void doLog(LogLevel logLevel, String message, Throwable throwable) {
		if(enabled && (logLevel.ordinal() >= loggerStore.getLogLevel().ordinal())) {
			PrintStream outputStream = loggerStore.getOutputStream();
			
			if(loggerStore.getLogClassNames()) {
				outputStream.println(className + ": " + message);
			}
			else {
				outputStream.println(message);
			}
			
			if (throwable != null) {
				throwable.printStackTrace(loggerStore.getErrorStream());
			}
			
			outputStream.flush();
		}
	}
	
	private void formatAndLog(LogLevel logLevel, String format, Object arg)
	{
		FormattingTuple formattingTuple = MessageFormatter.format(format, arg);
		doLog(logLevel, formattingTuple.getMessage(), formattingTuple.getThrowable());
	}
	
	private void formatAndLog(LogLevel logLevel, String format, Object arg1, Object arg2)
	{
		FormattingTuple formattingTuple = MessageFormatter.format(format, arg1, arg2);
		doLog(logLevel, formattingTuple.getMessage(), formattingTuple.getThrowable());
	}
	
	private void formatAndLog(LogLevel logLevel, String format, Object[] argArray)
	{
		FormattingTuple formattingTuple = MessageFormatter.arrayFormat(format, argArray);
		doLog(logLevel, formattingTuple.getMessage(), formattingTuple.getThrowable());
	}
	
	@Override
	public boolean isTraceEnabled()
	{
		return false;
	}
	
	@Override
	public void trace(String msg)
	{
		doLog(LogLevel.DEBUG, msg, null);
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
		doLog(LogLevel.DEBUG, msg, t);
	}
	
	@Override
	public boolean isDebugEnabled()
	{
		return (loggerStore.getLogLevel().ordinal() <= LogLevel.DEBUG.ordinal());
	}
	
	@Override
	public void debug(String msg)
	{
		doLog(LogLevel.DEBUG, msg, null);
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
		doLog(LogLevel.DEBUG, msg, t);
	}
	
	@Override
	public boolean isInfoEnabled()
	{
		return (loggerStore.getLogLevel().ordinal() <= LogLevel.INFO.ordinal());
	}
	
	@Override
	public void info(String msg)
	{
		doLog(LogLevel.INFO, msg, null);
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
		doLog(LogLevel.INFO, msg, t);
	}
	
	@Override
	public boolean isWarnEnabled()
	{
		return (loggerStore.getLogLevel().ordinal() <= LogLevel.WARN.ordinal());
	}
	
	@Override
	public void warn(String msg)
	{
		doLog(LogLevel.WARN, msg, null);
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
		doLog(LogLevel.WARN, msg, t);
	}
	
	@Override
	public boolean isErrorEnabled()
	{
		return (loggerStore.getLogLevel().ordinal() <= LogLevel.ERROR.ordinal());
	}
	
	@Override
	public void error(String msg)
	{
		doLog(LogLevel.ERROR, msg, null);
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
		doLog(LogLevel.ERROR, msg, t);
	}
}
