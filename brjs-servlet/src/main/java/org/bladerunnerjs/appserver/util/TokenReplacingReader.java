package org.bladerunnerjs.appserver.util;

import java.io.IOException;
import java.io.Reader;


// this class only reads a single char at a time from the SourceReader as it massively simplifies the code and we're not reading from large files as we might with the JS 'stripping' readers
public class TokenReplacingReader extends Reader
{
	
	public static final char tokenStart = '@';
	public static final char tokenEnd = '@';
	
	private TokenFinder tokenFinder;
	private Reader sourceReader;
	
	private boolean withinToken = false;
	private StringBuffer currentTokenString = new StringBuffer();;
	
	private StringBuffer tokenReplacementBuffer = new StringBuffer();
	
	private int currentOffset;
	private int maxCharactersToWrite;
	private int totalNumberOfCharsWritten;
	
	public TokenReplacingReader(TokenFinder tokenFinder, Reader sourceReader)
	{
		this.tokenFinder = tokenFinder;
		this.sourceReader = sourceReader;
	}
	
	@Override
	public int read(char[] destBuffer, int offset, int maxCharacters) throws IOException
	{
		currentOffset = offset;
		totalNumberOfCharsWritten = 0;
		maxCharactersToWrite = maxCharacters;
		
		int nextCharVal = -1;
		char nextChar = '\0';
		
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
				else if (nextChar == tokenEnd)
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
				if (nextChar == tokenStart)
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
			destBuffer[currentOffset++] = c;
			totalNumberOfCharsWritten ++;
		}
	}
	
	private void appendTokenString(char[] destBuffer) {
		addToCharBuffer(destBuffer, currentTokenString.toString().toCharArray());
		currentTokenString.setLength(0);
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
		tokenName = tokenName.substring(1, tokenName.length() - 1);
		String tokenReplacement = tokenFinder.findTokenValue(tokenName);
		if (tokenReplacement == null) {
			throw new IllegalArgumentException("No replacement found for token " + tokenName);
		}
		return tokenReplacement;
	}
	
}
