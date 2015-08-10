package org.bladerunnerjs.utility;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.appserver.util.MissingTokenHandler;
import org.bladerunnerjs.appserver.util.TokenReplacementException;
import org.bladerunnerjs.logger.LogLevel;

public class LoggingMissingTokenHandler implements MissingTokenHandler {

	public static final String NO_TOKEN_REPLACEMENT_MESSAGE = "No token replacement could be found for '%s' in the '%s' environment. JNDI must be configured to define a replacement for this token key.";
	private final Logger logger;
	private String environment;
	private LogLevel logLevel;

	public LoggingMissingTokenHandler(BRJS brjs, Class<?> loggerClass, String environment, LogLevel logLevel) {
		this.logger = brjs.logger(loggerClass);
		this.environment = (environment != null) ? environment : "default";
		this.logLevel = logLevel;
	}

	@Override
	public void handleNoTokenFound(String tokenName, TokenReplacementException thrownException) throws TokenReplacementException {
		switch (logLevel) {
			case CONSOLE:
				logger.println(NO_TOKEN_REPLACEMENT_MESSAGE, tokenName, environment);
				break;
			case DEBUG:
				logger.debug(NO_TOKEN_REPLACEMENT_MESSAGE, tokenName, environment);
				break;
			case ERROR:
				logger.error(NO_TOKEN_REPLACEMENT_MESSAGE, tokenName, environment);
				break;
			case INFO:
				logger.info(NO_TOKEN_REPLACEMENT_MESSAGE, tokenName, environment);
				break;
			case WARN:
				logger.warn(NO_TOKEN_REPLACEMENT_MESSAGE, tokenName, environment);
				break;
			
		}
	}

}
