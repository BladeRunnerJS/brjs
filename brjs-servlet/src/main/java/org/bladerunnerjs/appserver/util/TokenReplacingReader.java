package org.bladerunnerjs.appserver.util;

import java.io.IOException;
import java.io.Reader;


public class TokenReplacingReader extends Reader
{
	private static final int MAX_SINGLE_WRITE = 3;
	public static final char tokenStart = '@';
	public static final char tokenEnd = '@';
	private JndiTokenFinder tokenFinder;
	private Reader sourceReader;
	
	private int nextCharPos = 0;
	private int lastCharPos = 0;
	private boolean withinToken = false;
	private StringBuffer tokenString;
	
	public TokenReplacingReader(JndiTokenFinder tokenFinder, Reader sourceReader)
	{
		this.tokenFinder = tokenFinder;
		this.sourceReader = sourceReader;
	}
	
	@Override
	public int read(char[] destBuffer, int offset, int maxCharacters) throws IOException
	{
		if(lastCharPos == -1) {
			return -1;
		}
		
		int currentOffset = offset;
		int maxOffset = offset + maxCharacters - (MAX_SINGLE_WRITE - 1);
		char nextChar = '\0';
		char[] sourceBuffer = new char[4096];
		
		while(currentOffset < maxOffset) {
			if(nextCharPos == lastCharPos) {
				nextCharPos = 0;
				lastCharPos = sourceReader.read(sourceBuffer, 0, sourceBuffer.length - 1);
				
				if(lastCharPos == -1) {
					break;
				}
			}
			
			nextChar = sourceBuffer[nextCharPos++];
			
			if (nextChar == '\r' && nextChar != '\n') {
				throw new IOException("Mac line endings detected. This type of line ending is not supported.");
			}

			
			if (withinToken)
			{
				if (isValidTokenChar(nextChar))
				{
					tokenString.append(nextChar);
				}
				else if (nextChar == tokenEnd)
				{
					withinToken = false;
					tokenString.append(nextChar);
					if (tokenString.length() <= 2)
					{
						currentOffset = addToCharBuffer(destBuffer, currentOffset, tokenString.toString().toCharArray());
					}
					else
					{
						String tokenReplacement = findTokenReplacement(tokenString.toString(), tokenFinder);
						if (tokenReplacement != null)
						{
							currentOffset = addToCharBuffer(destBuffer, currentOffset, tokenReplacement.toString().toCharArray());
						}
						else
						{
							throw new IllegalArgumentException("No replacement found for token " + tokenString);
						}
					}
				}
				else
				{
					withinToken = false;
					currentOffset = addToCharBuffer(destBuffer, currentOffset, tokenString.toString().toCharArray());
					currentOffset = addToCharBuffer(destBuffer, currentOffset, nextChar);
				}
			}
			else
			{
				if (nextChar == tokenStart)
				{
					withinToken = true;
					tokenString = new StringBuffer();
					tokenString.append(nextChar);
				}
				else
				{
					currentOffset = addToCharBuffer(destBuffer, currentOffset, nextChar);
				}
			}
				
		}
		
		int charsProvided = (currentOffset - offset);
		return (charsProvided == 0) ? -1 : charsProvided;
	}

	@Override
	public void close() throws IOException
	{
		sourceReader.close();		
	}
	
	private int addToCharBuffer(char[] destBuffer, int currentOffset, char... chars) {
		for (char c : chars) {
			destBuffer[currentOffset++] = c;
		}
		return currentOffset;
	}
	
	private boolean isValidTokenChar(char c)
	{
		return Character.isUpperCase(c) || c == '.';
	}

	private String findTokenReplacement(String tokenName, JndiTokenFinder tokenFinder)
	{
		tokenName = tokenName.substring(1, tokenName.length() - 1);
		String tokenReplacement = tokenFinder.findTokenValue(tokenName);		
		return tokenReplacement;
	}
	
}
