package org.bladerunnerjs.logger;

import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.logger.LogLevel;

public class RootConsoleLogger {
	public Map<LogLevel, Map<String, LogLevel>> logProfiles = new HashMap<>();
	
	private LogLevel currentLogLevel = LogLevel.WARN;
	private PrintStream printStream = System.out;
	private PrintStream errPrintStream = System.err;
	private boolean debugMode;
	
	public LogLevel getLogLevel() {
		return currentLogLevel;
	}
	
	public void setLogLevel(LogLevel logLevel) {
		currentLogLevel = logLevel;
	}
	
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
	
	public void log(LogLevel logLevel, String loggerName, String message, Throwable throwable) {
		LogLevel profileLogLevel = getProfileLogLevel(logLevel, loggerName);
		
		if(logLevel.ordinal() >= profileLogLevel.ordinal()) {
			String logPrefix = "";
			
			if((currentLogLevel == LogLevel.DEBUG) & debugMode) {
				logPrefix = new Timestamp(System.currentTimeMillis()) + " " + loggerName + " (" + logLevel + "): ";
			}
			else if(logLevel == LogLevel.WARN || logLevel == LogLevel.ERROR) {
				logPrefix = logLevel + ": ";
			}
			
			printStream.println(logPrefix + message);
			
			if (throwable != null) {
				throwable.printStackTrace(errPrintStream);
			}
			
			printStream.flush();
		}
	}
	
	private LogLevel getProfileLogLevel(LogLevel logLevel, String loggerName) {
		Map<String, LogLevel> logProfile = logProfiles.get(logLevel);
		LogLevel profileLogLevel = currentLogLevel;
		
		if(logProfile != null) {
			for(String packageName : logProfile.keySet()) {
				if(loggerName.startsWith(packageName)) {
					profileLogLevel = logProfile.get(packageName);
				}
			}
		}
		
		return profileLogLevel;
	}
}
