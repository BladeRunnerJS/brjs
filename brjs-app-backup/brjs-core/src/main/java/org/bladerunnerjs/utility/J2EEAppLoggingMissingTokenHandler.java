package org.bladerunnerjs.utility;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.appserver.util.ExceptionThrowingMissingTokenHandler;
import org.bladerunnerjs.appserver.util.MissingTokenHandler;
import org.bladerunnerjs.appserver.util.TokenReplacementException;
import org.bladerunnerjs.logger.LogLevel;

/**
 * Logs a warning if the app is a J2EE app (appDir/WEB-INF exists), otherwise throws an exception
 */
public class J2EEAppLoggingMissingTokenHandler implements MissingTokenHandler {

	private MissingTokenHandler exceptionThrowingHandler;
	private MissingTokenHandler loggingHandler;
	private BRJS brjs;

	public J2EEAppLoggingMissingTokenHandler(BRJS brjs, Class<?> loggerClass, String environment, LogLevel logLevel) {
		this.brjs = brjs;
		exceptionThrowingHandler = new ExceptionThrowingMissingTokenHandler();
		loggingHandler = new LoggingMissingTokenHandler(brjs, loggerClass, environment, logLevel);
	}

	@Override
	public void handleNoTokenFound(String appName, String tokenName, TokenReplacementException thrownException) throws TokenReplacementException {
		if (!brjs.app(appName).file("WEB-INF").isDirectory()) {
			exceptionThrowingHandler.handleNoTokenFound(appName, tokenName, thrownException);
			return;
		} else {
    		loggingHandler.handleNoTokenFound(appName, tokenName, thrownException);
		}
	}
	
}
