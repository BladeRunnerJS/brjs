package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerFactory;
import org.bladerunnerjs.core.log.LoggerType;

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