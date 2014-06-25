package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.logging.Logger;

public class MockLogger implements Logger {
	@Override
	public String getName() {
		return this.getClass().getName();
	}
	
	@Override
	public void fatal(String message, Object... params)
	{
		// do nothing
	}
	
	@Override
	public void error(String message, Object... params) {
		// do nothing
	}
	
	@Override
	public void warn(String message, Object... params) {
		// do nothing
	}
	
	@Override
	public void info(String message, Object... params) {
		// do nothing
	}
	
	@Override
	public void debug(String message, Object... params) {
		// do nothing
	}

	@Override
	public void println(String message, Object... params)
	{
		// do nothing
	}

	@Override
	public void console(String message, Object... params)
	{
		// do nothing
	}
	
}
