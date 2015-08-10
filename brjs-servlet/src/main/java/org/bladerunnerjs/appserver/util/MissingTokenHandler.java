package org.bladerunnerjs.appserver.util;

/*
 * This interface exists to decouple the TokenReplcementReader from BRJS since the class lives in brjs-servlet but the project cannot depend on BRJS since it's used in production
 * BRJS uses of the TokenReplacementReader can provide an implementation of NoTokenReplacementHandler that uses BRJS to log the error instead of throwing the exception
 */

public interface MissingTokenHandler {

    public void handleNoTokenFound(String tokenName, TokenReplacementException thrownException) throws TokenReplacementException;

}
