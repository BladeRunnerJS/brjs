package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.utility.LoggerFactory;

public class StubLoggerFactory implements LoggerFactory {
	@Override
	public Logger getLogger(Class<?> clazz) {
		return new MockLogger();
	}
}
