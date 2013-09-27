package org.bladerunnerjs.testing.model.engine;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerFactory;
import org.bladerunnerjs.core.log.LoggerType;

public class MockLoggerFactory implements LoggerFactory {
	@Override
	public Logger getLogger(LoggerType type, Class<?> clazz) {
		return new MockLogger();
	}
}
