package org.bladerunnerjs.logging;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.utility.LoggerFactory;


public class SLF4JLoggerFactory implements LoggerFactory
{
	private Map<String, Logger> loggers = new LinkedHashMap<String, Logger>();
	
	@Override
	public Logger getLogger(Class<?> clazz)
	{
		String className = clazz.getName();
		
		Logger logger = null;
		synchronized (loggers)
		{
			logger = loggers.get(className);
			if (logger == null)
			{
				logger = new SLF4JLogger(org.slf4j.LoggerFactory.getLogger(className), className);
				loggers.put(className, logger);
			}
		}
		return logger;
	}

}
