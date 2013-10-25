package org.bladerunnerjs.core.log;

import org.bladerunnerjs.logger.LogLevel;

public class NullLogConfigurator implements LogConfiguration {
	@Override
	public void setLogLevel(LogLevel logLevel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LogLevel getLogLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printTimestamps(boolean printTimestamps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printMessageSourceInfo(boolean showTimestamps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProfileBuilder ammendProfile(LogLevel printMessageSourceInfo) {
		// TODO Auto-generated method stub
		return null;
	}
}