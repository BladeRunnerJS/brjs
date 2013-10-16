package com.caplin.cutlass.testing;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerFactory;
import org.bladerunnerjs.core.log.LoggerType;

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
