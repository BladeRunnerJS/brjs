package org.bladerunnerjs.api.logging;

/**
 * Used for logging messages from the BladeRunnerJS model or plugins. All methods behave in a similar way to {@link String#format(String, Object...)} 
 * where the first argument represents the format String and the subsequent arguments are values used to replace the format string placeholders. 
 * 
 * {@link Logger#error}, {@link Logger#warn}, {@link Logger#info} &amp; {@link Logger#debug} all correspond to the various log levels that can be configured and are output using a logging framework.
 * {@link Logger#println} &amp; {@link Logger#console} both log directly to {@link System#err}. 
 *
 */
public interface Logger
{
	String getName();

	public void error(String message, Object... params);
	public void warn(String message, Object... params);
	public void info(String message, Object... params);
	public void debug(String message, Object... params);
	
	public void println(String message, Object... params);
	public void console(String message, Object... params);
}
