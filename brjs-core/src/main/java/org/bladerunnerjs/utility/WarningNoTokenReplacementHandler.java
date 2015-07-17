package org.bladerunnerjs.utility;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.appserver.util.NoTokenReplacementHandler;
import org.bladerunnerjs.appserver.util.TokenReplacementException;

public class WarningNoTokenReplacementHandler implements NoTokenReplacementHandler {

	private final Logger logger;

	public WarningNoTokenReplacementHandler(BRJS brjs, Class<?> loggerClass, String environment) {
		this.logger = brjs.logger(loggerClass);
	}

	@Override
	public void handleNoTokenFound(String tokenName, TokenReplacementException thrownException) throws TokenReplacementException {
		logger.warn("No token replacement could be found for '%s' in the '%s' environment.  JNDI must be configured to define a replacement for this token.");
	}

}
