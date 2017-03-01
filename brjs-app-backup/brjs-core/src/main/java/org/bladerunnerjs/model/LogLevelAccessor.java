package org.bladerunnerjs.model;

import org.bladerunnerjs.logger.LogLevel;

public interface LogLevelAccessor {
	LogLevel getLogLevel();
	void setLogLevel(LogLevel logLevel);
}
