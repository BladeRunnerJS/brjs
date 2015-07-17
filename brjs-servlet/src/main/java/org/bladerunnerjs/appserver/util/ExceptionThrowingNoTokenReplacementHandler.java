package org.bladerunnerjs.appserver.util;

public class ExceptionThrowingNoTokenReplacementHandler implements  NoTokenReplacementHandler {

    @Override
    public void handleNoTokenFound(String tokenName, TokenReplacementException thrownException) throws TokenReplacementException {
        throw thrownException;
    }
}
