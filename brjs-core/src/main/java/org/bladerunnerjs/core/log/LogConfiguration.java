package org.bladerunnerjs.core.log;

import org.bladerunnerjs.logger.LogLevel;

public interface LogConfiguration {
	void setLogLevel(LogLevel logLevel);
	LogLevel getLogLevel();
	void printTimestamps(boolean printTimestamps);
	void printMessageSourceInfo(boolean showTimestamps);
	ProfileBuilder ammendProfile(LogLevel printMessageSourceInfo);
}
