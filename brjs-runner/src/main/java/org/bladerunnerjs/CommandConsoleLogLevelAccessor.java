package org.bladerunnerjs;

import org.bladerunnerjs.model.LogLevelAccessor;

import org.bladerunnerjs.logger.LogLevel;
import org.bladerunnerjs.logger.RootConsoleLogger;

public class CommandConsoleLogLevelAccessor implements LogLevelAccessor {
	private final RootConsoleLogger rootLogger;

	public CommandConsoleLogLevelAccessor(RootConsoleLogger rootLogger) {
		this.rootLogger = rootLogger;
	}
	
	@Override
	public LogLevel getLogLevel() {
		return rootLogger.getLogLevel();
	}
	
	@Override
	public void setLogLevel(LogLevel logLevel) {
		rootLogger.setLogLevel(logLevel);
	}
}
