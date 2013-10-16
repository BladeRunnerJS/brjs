package org.bladerunnerjs.logger;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class ConsoleLoggerFactory implements ILoggerFactory
{
	private RootConsoleLogger rootLogger = new RootConsoleLogger();
	
	public RootConsoleLogger getRootLogger() {
		return rootLogger;
	}
	
	@Override
	public Logger getLogger(String name) {
		return new ConsoleLogger(name, rootLogger);
	}
}
