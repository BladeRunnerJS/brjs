package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.logging.LoggerType;

public class MockLoggerFactory implements LoggerFactory {
	@Override
	public Logger getLogger(LoggerType type, Class<?> clazz) {
		return new MockLogger();
	}
}
