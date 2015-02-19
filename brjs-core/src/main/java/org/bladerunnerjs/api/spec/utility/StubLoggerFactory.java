package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.logging.LoggerFactory;

public class StubLoggerFactory implements LoggerFactory {
	@Override
	public Logger getLogger(Class<?> clazz) {
		return new MockLogger();
	}
}
