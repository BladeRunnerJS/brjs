package org.bladerunnerjs.core.console;


public interface ConsoleWriter
{
	public void println(String message, Object... params);
	public void println();
	public void flush();
}
