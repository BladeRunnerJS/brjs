package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerFactory;

public class TestLoggerFactory implements LoggerFactory
{
	LogMessageStore logStore;
	
	public TestLoggerFactory(LogMessageStore logStore)
	{
		this.logStore = logStore;
	}

	public Logger getLogger(Class<?> clazz)
	{
		return new TestLogger( clazz.getPackage().getName(), logStore);
	}
}