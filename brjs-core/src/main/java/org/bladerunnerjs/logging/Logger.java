package org.bladerunnerjs.logging;

public interface Logger
{

	String getName();
	
	public void error(String message, Object... params);

	public void warn(String message, Object... params);
	
	public void println(String message, Object... params);
	public void console(String message, Object... params);

	public void info(String message, Object... params);

	public void debug(String message, Object... params);
}
