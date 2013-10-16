package org.bladerunnerjs.core.log;

import java.util.HashMap;
import java.util.Map;


public class SLF4JLoggerFactory implements LoggerFactory
{

	private Map<String, Logger> loggers = new HashMap<String, Logger>();

	public Logger getLogger(LoggerType type, Class<?> clazz)
	{
		String name = type.getTypedLoggerName(clazz);

		Logger logger = null;
		synchronized (loggers)
		{
			logger = loggers.get(name);
			if (logger == null)
			{
				logger = new SLF4JLogger(org.slf4j.LoggerFactory.getLogger(name), name);
				loggers.put(name, logger);
			}
		}
		return logger;
	}

}
