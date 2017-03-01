package org.bladerunnerjs.utility;

import org.bladerunnerjs.api.logging.Logger;

public interface LoggerFactory
{

	public Logger getLogger(Class<?> clazz);
}
