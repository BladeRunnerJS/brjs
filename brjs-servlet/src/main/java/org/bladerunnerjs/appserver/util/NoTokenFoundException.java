package org.bladerunnerjs.appserver.util;

public class NoTokenFoundException extends Exception {

    public NoTokenFoundException(String tokenName, Class<? extends TokenFinder> tokenFinder) {
        this(tokenName, tokenFinder, null);
    }

    public NoTokenFoundException(String tokenName, Class<? extends TokenFinder> tokenFinder, Throwable cause) {
        super( String.format( "An error occurred when the token finder '%s' attempted to locate a replacement for the the token '%s'.",
                tokenFinder.getSimpleName(), tokenName ), cause);
    }

}
