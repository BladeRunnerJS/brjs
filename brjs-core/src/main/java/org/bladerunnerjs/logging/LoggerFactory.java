package org.bladerunnerjs.logging;

public interface LoggerFactory
{

	public Logger getLogger(LoggerType type, Class<?> clazz);
}
