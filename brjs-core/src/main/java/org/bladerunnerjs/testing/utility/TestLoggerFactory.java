package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.logging.LoggerType;

public class TestLoggerFactory implements LoggerFactory
{
	LogMessageStore logStore;
	
	public TestLoggerFactory(LogMessageStore logStore)
	{
		this.logStore = logStore;
	}

	public Logger getLogger(LoggerType type, Class<?> clazz)
	{
		return new TestLogger( type.getTypedLoggerName(clazz), logStore);
	}
}