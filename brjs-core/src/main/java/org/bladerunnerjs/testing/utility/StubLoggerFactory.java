package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerFactory;

public class StubLoggerFactory implements LoggerFactory {
	@Override
	public Logger getLogger(Class<?> clazz) {
		return new MockLogger();
	}
}
