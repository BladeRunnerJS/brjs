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
	private StringBuffer tokenReplacementBuffer = new StringBuffer();
	
	private int currentOffset;
	private int maxCharactersToWrite;
	private int totalNumberOfCharsWritten;

	public TokenReplacingReader(TokenFinder userTokenFinder, Reader sourceReader) {
		this(null, userTokenFinder, sourceReader, new ExceptionThrowingMissingTokenHandler());
	}

	public TokenReplacingReader(TokenFinder userTokenFinder, Reader sourceReader, MissingTokenHandler replacementHandler) {
		this(null, userTokenFinder, sourceReader, replacementHandler);
	}
	
    public TokenReplacingReader(TokenFinder brjsTokenFinder, TokenFinder tokenFinder, Reader sourceReader, MissingTokenHandler replacementHandler) {
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
			appendBufferedTokens(destBuffer);
		}
		if (tokenReplacementBuffer.length() > 0) {
			appendTokenReplacement(destBuffer);
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
						appendTokenString(destBuffer);
						currentTokenString.setLength(0);
					}
					else
					{
						tokenReplacementBuffer.ensureCapacity(0);
						tokenReplacementBuffer.append( findTokenReplacement(currentTokenString.toString()) );
						currentTokenString.setLength(0);
						appendTokenReplacement(destBuffer);
					}
				}
				else
				{
					withinToken = false;
					appendTokenString(destBuffer);
					currentTokenString.setLength(0);
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
	
	private void appendTokenString(char[] destBuffer) {
		addToCharBuffer(destBuffer, currentTokenString.toString().toCharArray());
		currentTokenString.setLength(0);
	}
	
	private void appendBufferedTokens(char[] destBuffer) {
		while (readTokensBuffer.length() > 0 && canWriteMoreChars()) {
			addToCharBuffer(destBuffer, readTokensBuffer.charAt(0));
			readTokensBuffer.delete(0, 1);
		}
	}
	
	private void appendTokenReplacement(char[] destBuffer) {
		while (tokenReplacementBuffer.length() > 0 && canWriteMoreChars()) {
			addToCharBuffer(destBuffer, tokenReplacementBuffer.charAt(0));
			tokenReplacementBuffer.delete(0, 1);
		}
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
                replacementHandler.handleNoTokenFound( tokenKey, ex );
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
