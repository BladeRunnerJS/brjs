package org.bladerunnerjs.appserver.util;

public class TokenReplacementException extends Exception {

    public TokenReplacementException(String tokenName, Class<? extends TokenFinder> tokenFinder) {
        super(String.format("The token finder '%s' could not find a replacement for the token '%s'.",
                tokenFinder.getSimpleName(), tokenName), null);
    }

    public TokenReplacementException(String tokenName, Class<? extends TokenFinder> tokenFinder, Throwable cause) {
        super( String.format( "An error occurred when the token finder '%s' attempted to locate a replacement for the the token '%s'.",
                tokenFinder.getSimpleName(), tokenName ), cause);
    }

}
