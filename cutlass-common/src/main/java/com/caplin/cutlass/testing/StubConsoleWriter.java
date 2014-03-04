package com.caplin.cutlass.testing;

import org.bladerunnerjs.console.ConsoleWriter;

public class StubConsoleWriter implements ConsoleWriter {
	@Override
	public void println(String message, Object... params) {
	}
	
	@Override
	public void println() {
		println("");
	}
	
	@Override
	public void flush() {
	}
}
