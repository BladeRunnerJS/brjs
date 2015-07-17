package org.bladerunnerjs.appserver.util;

public class IgnoreExceptionNoTokenReplacementHandler implements NoTokenReplacementHandler {

    @Override
    public void handleNoTokenFound(String tokenName, TokenReplacementException thrownException) throws TokenReplacementException {
        // ignore
    }
}
