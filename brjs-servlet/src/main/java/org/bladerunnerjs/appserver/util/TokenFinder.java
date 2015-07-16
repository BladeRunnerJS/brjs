package org.bladerunnerjs.appserver.util;


public interface TokenFinder
{
    /**
     * Finds the replacement for a given token key
     * @param tokenName the name, or key, of the token to find a replacement for
     * @return the token replacement
     * @throws NoTokenFoundException if no replacement could be found
     */
	public String findTokenValue(String tokenName) throws NoTokenFoundException;
}
