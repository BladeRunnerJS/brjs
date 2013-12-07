package org.bladerunnerjs.logging;

import org.bladerunnerjs.logger.LogLevel;

public interface LogConfiguration {
	void setLogLevel(LogLevel logLevel);
	LogLevel getLogLevel();
	void printTimestamps(boolean printTimestamps);
	void printMessageSourceInfo(boolean showTimestamps);
	ProfileBuilder ammendProfile(LogLevel printMessageSourceInfo);
}
