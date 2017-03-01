package org.bladerunnerjs.api.model.exception.request;

import java.util.regex.Pattern;

/**
 * Thrown when the specified token-value pair does not match the required format. 
*/

public class MalformedTokenException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public MalformedTokenException(String tokenName, String tokenValue, Pattern tokenPattern) {
		super(String.format("Token '%s' with value '%s' does not match pattern '%s'.", tokenName, tokenValue, tokenPattern.toString()));
	}
}
