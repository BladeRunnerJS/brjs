package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.logging.Logger;

public class MockLogger implements Logger {
	@Override
	public String getName() {
		return this.getClass().getName();
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
