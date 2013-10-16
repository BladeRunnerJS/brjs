package org.bladerunnerjs;

import java.util.HashMap;

import org.bladerunnerjs.core.log.LogConfiguration;
import org.bladerunnerjs.core.log.ProfileBuilder;
import org.bladerunnerjs.logger.RootConsoleLogger;
import org.bladerunnerjs.logger.LogLevel;

public class ConsoleLoggerConfigurator implements LogConfiguration {
	private final RootConsoleLogger rootLogger;
	
	public ConsoleLoggerConfigurator(RootConsoleLogger rootLogger) {
		this.rootLogger = rootLogger;
	}
	
	@Override
	public void setLogLevel(LogLevel logLevel) {
		rootLogger.setLogLevel(logLevel);
	}
	
	@Override
	public LogLevel getLogLevel() {
		return rootLogger.getLogLevel();
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
	public ProfileBuilder ammendProfile(LogLevel logLevel) {
		rootLogger.logProfiles.put(logLevel, new HashMap<String, LogLevel>());
		return new ProfileBuilder(rootLogger.logProfiles.get(logLevel));
	}
}