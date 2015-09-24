package org.bladerunnerjs.appserver.util;


public interface TokenFinder
{
    /**
     * Finds the replacement for a given token key
     * @param tokenName the name, or key, of the token to find a replacement for
     * @return the token replacement
     * @throws TokenReplacementException if no replacement could be found or there was an error finding the replacement
     */
	public String findTokenValue(String tokenName) throws TokenReplacementException;
}
