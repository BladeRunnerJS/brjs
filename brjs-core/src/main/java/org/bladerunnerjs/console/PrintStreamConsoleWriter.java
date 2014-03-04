package org.bladerunnerjs.console;

import java.io.PrintStream;

public class PrintStreamConsoleWriter implements ConsoleWriter
{

	private static final String NEWLINE = System.getProperty("line.separator");
	private PrintStream out;

	public PrintStreamConsoleWriter(PrintStream out)
	{
		this.out = out;
	}
	
	@Override
	public void println(String message, Object... params)
	{
		out.print( String.format(message+NEWLINE, params) );
		out.flush();
	}
	
	@Override
	public void println() {
		println("");
	}
	
	@Override
	public void flush()
	{
		out.flush();
	}

}
