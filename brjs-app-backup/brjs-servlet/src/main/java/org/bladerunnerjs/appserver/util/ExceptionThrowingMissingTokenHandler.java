package org.bladerunnerjs.appserver.util;

public class ExceptionThrowingMissingTokenHandler implements  MissingTokenHandler {

    @Override
    public void handleNoTokenFound(String appName, String tokenName, TokenReplacementException thrownException) throws TokenReplacementException {
        throw thrownException;
    }
}
