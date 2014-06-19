package org.bladerunnerjs.logger;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class ConsoleLoggerStore implements ILoggerFactory
{
	private final Map<String, ConsoleLogger> loggers = new HashMap<>();
	private List<String> allWhitelistedPackages = new ArrayList<>();
	private LogLevel currentLogLevel = LogLevel.WARN;
	private boolean logClassNames = false;
	private PrintStream stdout = System.out;
	private PrintStream stderr = System.err;
	
	{
		allWhitelistedPackages.add("org.bladerunnerjs");
	}
	
	@Override
	public Logger getLogger(String name) {
		if(!loggers.containsKey(name)) {
			loggers.put(name, new ConsoleLogger(name, this));
			loggers.get(name).setWhiteListedPackages(allWhitelistedPackages);
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
		allWhitelistedPackages = new ArrayList<>(whitelistedPackages);
		allWhitelistedPackages.add("org.bladerunnerjs");
		
		for(ConsoleLogger logger : loggers.values()) {
			logger.setWhiteListedPackages(allWhitelistedPackages);
		}
	}
	
	public void setLogClassNames(boolean logClassNames) {
		this.logClassNames  = logClassNames;
	}
	
	public boolean getLogClassNames() {
		return logClassNames;
	}
	
	public void setOutputStreams(PrintStream stdout, PrintStream stderr) {
		this.stdout = stdout;
		this.stderr = stderr;
	}
	
	public PrintStream getOutputStream() {
		return stdout;
	}
	
	public PrintStream getErrorStream() {
		return stderr;
	}
}
