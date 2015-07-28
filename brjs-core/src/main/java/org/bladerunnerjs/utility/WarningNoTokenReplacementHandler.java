package org.bladerunnerjs.utility;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.appserver.util.NoTokenReplacementHandler;
import org.bladerunnerjs.appserver.util.TokenReplacementException;

public class WarningNoTokenReplacementHandler implements NoTokenReplacementHandler {

	public static final String NO_TOKEN_REPLACEMENT_MESSAGE = "No token replacement could be found for '%s' in the '%s' environment. JNDI must be configured to define a replacement for this token key.";
	private final Logger logger;
	private String environment;

	public WarningNoTokenReplacementHandler(BRJS brjs, Class<?> loggerClass, String environment) {
		this.logger = brjs.logger(loggerClass);
		this.environment = environment;
	}

	@Override
	public void handleNoTokenFound(String tokenName, TokenReplacementException thrownException) throws TokenReplacementException {
		logger.info(NO_TOKEN_REPLACEMENT_MESSAGE, tokenName, environment);
	}

}
