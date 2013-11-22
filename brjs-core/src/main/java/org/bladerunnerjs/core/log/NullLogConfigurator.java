package org.bladerunnerjs.core.log;

import org.bladerunnerjs.logger.LogLevel;

public class NullLogConfigurator implements LogConfiguration {
	@Override
	public void setLogLevel(LogLevel logLevel) {
	}

	@Override
	public LogLevel getLogLevel() {
		return null;
	}

	@Override
	public void printTimestamps(boolean printTimestamps) {
	}

	@Override
	public void printMessageSourceInfo(boolean showTimestamps) {
	}

	@Override
	public ProfileBuilder ammendProfile(LogLevel printMessageSourceInfo) {
		return null;
	}
}