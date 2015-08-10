package org.bladerunnerjs.appserver.util;

public class ExceptionThrowingMissingTokenHandler implements  MissingTokenHandler {

    @Override
    public void handleNoTokenFound(String tokenName, TokenReplacementException thrownException) throws TokenReplacementException {
        throw thrownException;
    }
}
