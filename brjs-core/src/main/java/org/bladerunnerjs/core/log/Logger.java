package org.bladerunnerjs.core.log;

public interface Logger
{

	String getName();
	
	public void fatal(String message, Object... params);
	
	public void error(String message, Object... params);

	public void warn(String message, Object... params);

	public void info(String message, Object... params);

	public void debug(String message, Object... params);
}
