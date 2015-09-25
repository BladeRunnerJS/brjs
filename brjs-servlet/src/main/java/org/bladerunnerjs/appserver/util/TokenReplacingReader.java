package org.bladerunnerjs.appserver.util;

import java.io.IOException;
import java.io.Reader;


// this class only reads a single char at a time from the SourceReader as it massively simplifies the code and we're not reading from large files as we might with the JS 'stripping' readers
public class TokenReplacingReader extends Reader
{
	
	public static final char TOKEN_START = '@';
	public static final char TOKEN_END = '@';
	public static final String BRJS_KEY_PREFIX = "BRJS.";
	public static final String NO_BRJS_TOKEN_CONFIGURED_MESSAGE = "No token finder has been configured for '"+BRJS_KEY_PREFIX+"*' tokens. Only 'user' tokens can be used at this location";
	public static final String NO_BRJS_TOKEN_FOUND_MESSAGE = "The token '%s' is an invalid BRJS system token.";

	private final TokenFinder brjsTokenFinder;
    private final TokenFinder userTokenFinder;
	private final Reader sourceReader;
    private final MissingTokenHandler replacementHandler;

    private boolean withinToken = false;
	private StringBuffer currentTokenString = new StringBuffer();;
	
	private StringBuffer readTokensBuffer = new StringBuffer();
	
	private int currentOffset;
	private int maxCharactersToWrite;
	private int totalNumberOfCharsWritten;
	private String appName;

	public TokenReplacingReader(String appName, TokenFinder userTokenFinder, Reader sourceReader) {
		this(appName, null, userTokenFinder, sourceReader, new ExceptionThrowingMissingTokenHandler());
	}

	public TokenReplacingReader(String appName, TokenFinder userTokenFinder, Reader sourceReader, MissingTokenHandler replacementHandler) {
		this(appName, null, userTokenFinder, sourceReader, replacementHandler);
	}
	
    public TokenReplacingReader(String appName, TokenFinder brjsTokenFinder, TokenFinder tokenFinder, Reader sourceReader, MissingTokenHandler replacementHandler) {
    	this.appName = appName;
    	this.userTokenFinder = tokenFinder;
    	this.brjsTokenFinder = brjsTokenFinder;
        this.sourceReader = sourceReader;
        this.replacementHandler = replacementHandler;
    }

    @Override
	public int read(char[] destBuffer, int offset, int maxCharacters) throws IOException
	{
		currentOffset = offset;
		totalNumberOfCharsWritten = 0;
		maxCharactersToWrite = maxCharacters;
		
		int nextCharVal = -1;
		char nextChar = '\0';
		
		if (readTokensBuffer.length() > 0) {
			addToCharBuffer(destBuffer, readTokensBuffer);
		}
		
		while (canWriteMoreChars()) {
			nextCharVal = sourceReader.read();
			
			if (nextCharVal == -1) {
				break;
			}
			nextChar = (char) nextCharVal;
			
			if (withinToken)
			{
				if (isValidTokenChar(nextChar))
				{
					currentTokenString.append(nextChar);
				}
				else if (nextChar == TOKEN_END)
				{
					withinToken = false;
					currentTokenString.append(nextChar);
					if (currentTokenString.length() <= 2)
					{
						addToCharBuffer(destBuffer, currentTokenString);
					}
					else
					{
						addToCharBuffer(destBuffer, findTokenReplacement(currentTokenString.toString()).toCharArray());
						currentTokenString.setLength(0);
					}
				}
				else
				{
					withinToken = false;
					addToCharBuffer(destBuffer, currentTokenString);
					addToCharBuffer(destBuffer, nextChar);
				}
			}
			else
			{
				if (nextChar == TOKEN_START)
				{
					withinToken = true;
					currentTokenString.setLength(0);
					currentTokenString.append(nextChar);
				}
				else
				{
					addToCharBuffer(destBuffer, nextChar);
				}
			}
				
		}
		
		if (currentTokenString.length() > 0) {
			addToCharBuffer(destBuffer, currentTokenString.toString().toCharArray());
			currentTokenString.setLength(0);
		}
		
		int charsProvided = (currentOffset - offset);
		return (charsProvided == 0) ? -1 : charsProvided;
	}

	@Override
	public void close() throws IOException
	{
		sourceReader.close();		
	}
	
	private boolean canWriteMoreChars() {
		return totalNumberOfCharsWritten < maxCharactersToWrite;
	}
	
	private void addToCharBuffer(char[] destBuffer, char... chars) {
		for (char c : chars) {
			if (canWriteMoreChars()) {
				destBuffer[currentOffset++] = c;
				totalNumberOfCharsWritten ++;
			} else {
				readTokensBuffer.append(c);
			}
		}
	}
	
	private void addToCharBuffer(char[] destBuffer, StringBuffer buffer) {
		addToCharBuffer(destBuffer, buffer.toString().toCharArray());
		buffer.setLength(0);
	}
	
	private boolean isValidTokenChar(char c)
	{
		return Character.isUpperCase(c) || c == '.';
	}

	private String findTokenReplacement(String tokenName)
	{
		String tokenKey = tokenName.substring(1, tokenName.length() - 1);
        try {
        	String tokenValue;
        	if (tokenKey.startsWith(BRJS_KEY_PREFIX)) {
        		tokenValue = getBrjsToken(tokenKey);
        	} else {
        		tokenValue = userTokenFinder.findTokenValue(tokenKey);
        	}
            if (tokenValue == null) {
                return "";
            }
            return tokenValue;
        } catch (TokenReplacementException ex) {
            try {
                replacementHandler.handleNoTokenFound( appName, tokenKey, ex );
                return tokenName;
            } catch (TokenReplacementException handlerThrownEx) {
                throw new IllegalArgumentException(handlerThrownEx);
            }
        }
    }

	private String getBrjsToken(String tokenKey)
	{
		if (brjsTokenFinder == null) {
			throw new IllegalArgumentException(NO_BRJS_TOKEN_CONFIGURED_MESSAGE);
		}
		try {
			return brjsTokenFinder.findTokenValue(tokenKey);
		 } catch (TokenReplacementException ex) {
			 throw new IllegalArgumentException( String.format(NO_BRJS_TOKEN_FOUND_MESSAGE, tokenKey), ex );
        }
	}
	
}
