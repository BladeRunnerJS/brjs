package org.bladerunnerjs.runner;

import org.bladerunnerjs.model.LogLevelAccessor;
import org.bladerunnerjs.logger.ConsoleLoggerStore;
import org.bladerunnerjs.logger.LogLevel;

public class CommandConsoleLogLevelAccessor implements LogLevelAccessor {
	private final ConsoleLoggerStore consoleLoggerStore;

	public CommandConsoleLogLevelAccessor(ConsoleLoggerStore consoleLoggerStore) {
		this.consoleLoggerStore = consoleLoggerStore;
	}
	
	@Override
	public LogLevel getLogLevel() {
		return consoleLoggerStore.getLogLevel();
	}
	
	@Override
	public void setLogLevel(LogLevel logLevel) {
		consoleLoggerStore.setLogLevel(logLevel);
	}
}
