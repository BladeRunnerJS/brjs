package org.slf4j.impl;

import org.bladerunnerjs.logger.ConsoleLoggerStore;
import org.slf4j.spi.LoggerFactoryBinder;


public class StaticLoggerBinder implements LoggerFactoryBinder
{
	// Note: version of SLF4J compiled against -- to avoid constant folding by the compiler, this field must *not* be final
	public static String REQUESTED_API_VERSION = "1.6"; // !final
	private static final StaticLoggerBinder singleton = new StaticLoggerBinder();
	private final ConsoleLoggerStore loggerFactory = new ConsoleLoggerStore();
	
	public static StaticLoggerBinder getSingleton() {
		return singleton;
	}
	
	@Override
	public ConsoleLoggerStore getLoggerFactory() {
		return loggerFactory;
	}
	
	@Override
	public String getLoggerFactoryClassStr() {
		return ConsoleLoggerStore.class.getName();
	}
}