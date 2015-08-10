package org.bladerunnerjs.appserver.util;

public class IgnoreExceptionMissingTokenHandler implements MissingTokenHandler {

    @Override
    public void handleNoTokenFound(String tokenName, TokenReplacementException thrownException) throws TokenReplacementException {
        // ignore
    }
}
