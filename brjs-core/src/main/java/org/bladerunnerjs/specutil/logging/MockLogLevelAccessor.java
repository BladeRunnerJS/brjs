package org.bladerunnerjs.specutil.logging;

import org.bladerunnerjs.logger.LogLevel;
import org.bladerunnerjs.model.LogLevelAccessor;

public class MockLogLevelAccessor implements LogLevelAccessor {
	private LogLevel logLevel = LogLevel.INFO;
	
	@Override
	public LogLevel getLogLevel() {
		return logLevel;
	}
	
	@Override
	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}
}