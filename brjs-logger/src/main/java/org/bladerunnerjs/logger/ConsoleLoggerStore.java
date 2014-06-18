package org.bladerunnerjs.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class ConsoleLoggerStore implements ILoggerFactory
{
	private final Map<String, ConsoleLogger> loggers = new HashMap<>();
	private List<String> whitelistedPackages = new ArrayList<>();
	private LogLevel currentLogLevel = LogLevel.WARN;
	
	{
		whitelistedPackages.add("org.bladerunnerjs");
	}
	
	@Override
	public Logger getLogger(String name) {
		if(!loggers.containsKey(name)) {
			loggers.put(name, new ConsoleLogger(name, this));
			loggers.get(name).setWhiteListedPackages(whitelistedPackages);
		}
		
		return loggers.get(name);
	}
	
	public LogLevel getLogLevel() {
		return currentLogLevel;
	}
	
	public void setLogLevel(LogLevel logLevel) {
		currentLogLevel = logLevel;
	}
	
	public void setWhitelistedPackages(List<String> whitelistedPackages) {
		whitelistedPackages.add("org.bladerunnerjs");
		this.whitelistedPackages = whitelistedPackages;
		
		for(ConsoleLogger logger : loggers.values()) {
			logger.setWhiteListedPackages(whitelistedPackages);
		}
	}
}
