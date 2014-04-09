package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;


public abstract class AbstractStrippingReader extends Reader
{
	
	protected StringBuffer overflowBuffer = new StringBuffer();
	
	private Reader sourceReader;
	private char previousChar;
	
	public AbstractStrippingReader(Reader sourceReader)
	{
		this.sourceReader = sourceReader;
	}
	
	@Override
	public void close() throws IOException
	{
		sourceReader.close();
	}
	
	abstract int getMaxSingleWrite();
	
	protected abstract char[] handleNextCharacter(char nextChar, char previousChar) throws IOException;
	
	@Override
	public int read(char[] buff, int offset, int maxCharacters) throws IOException
	{
		int charactersWritten = 0;
		int maxEfficientCharacters = Math.max(1, maxCharacters - getMaxSingleWrite() + 1);
		int nextInt;
		
		if(overflowBuffer.length() > 0)
		{
			String overflowString = overflowBuffer.toString();
			overflowBuffer.setLength(0);
			charactersWritten = write(overflowString.toCharArray(), buff, offset, maxCharacters, charactersWritten);
		}
		
		while((charactersWritten < maxEfficientCharacters) && ((nextInt = sourceReader.read()) != -1))
		{
			char nextChar = (char) nextInt;
			
			ensureCharactersDontMatchMacLineEndings(previousChar, nextChar);
			
			char[] charsToWrite = handleNextCharacter(nextChar, previousChar);
			
			charactersWritten = write(charsToWrite, buff, offset, maxCharacters, charactersWritten);
			
			previousChar = nextChar;
		}
		
		return (charactersWritten == 0) ? -1 : charactersWritten;
	}
	
	
	private int write(char[] characters, char[] buff, int offset, int maxCharacters, int charactersWritten)
	{
		for(int i = 0, l = characters.length; i < l; ++i)
		{
			charactersWritten = write(characters[i], buff, offset, maxCharacters, charactersWritten);
		}
		
		return charactersWritten;
	}
	
	private int write(char nextChar, char[] buff, int offset, int maxCharacters, int charactersWritten)
	{
		if(charactersWritten < maxCharacters)
		{
			buff[offset + charactersWritten] = nextChar;
			charactersWritten++;
		}
		else
		{
			overflowBuffer.append(nextChar);
		}
		
		return charactersWritten;
	}
	
	private void ensureCharactersDontMatchMacLineEndings(char previousChar, char nextChar) throws IOException
	{
		if (previousChar == '\r' && nextChar != '\n')
		{
			throw new IOException("Mac line endings detected. This type of line ending is not supported.");
		}
	}

}
