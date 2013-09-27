package org.bladerunnerjs.core.log;

public interface LoggerFactory
{

	public Logger getLogger(LoggerType type, Class<?> clazz);
}
