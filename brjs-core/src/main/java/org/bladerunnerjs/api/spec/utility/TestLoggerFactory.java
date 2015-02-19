package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.logging.LoggerFactory;

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