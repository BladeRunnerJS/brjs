package com.caplin.cutlass.testing;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.logging.LoggerType;

public class StubLoggerFactory implements LoggerFactory {
	@Override
	public Logger getLogger(LoggerType type, Class<?> clazz) {
		return new Logger() {
			@Override
			public String getName() {
				return getClass().getSimpleName();
			}
			
			@Override
			public void debug(String message, Object... params) {
			}
			
			@Override
			public void info(String message, Object... params) {
			}
			
			@Override
			public void warn(String message, Object... params) {
			}
			
			@Override
			public void error(String message, Object... params) {
			}
			
			@Override
			public void fatal(String message, Object... params) {
			}
		};
	}
}
