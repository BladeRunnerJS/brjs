package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;


public abstract class AbstractStrippingReader extends Reader
{
	
	protected StringBuffer overflowBuffer = new StringBuffer();
	
	protected int write(String characters, char[] buff, int offset, int maxCharacters, int charactersWritten)
	{
		for(int i = 0, l = characters.length(); i < l; ++i)
		{
			charactersWritten = write(characters.charAt(i), buff, offset, maxCharacters, charactersWritten);
		}
		
		return charactersWritten;
	}
	
	protected int write(char nextChar, char[] buff, int offset, int maxCharacters, int charactersWritten)
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
	
	protected void ensureCharactersDontMatchMacLineEndings(char previousChar, char nextChar) throws IOException
	{
		if (previousChar == '\r' && nextChar != '\n')
		{
			throw new IOException("Mac line endings detected. This type of line ending is not supported.");
		}
	}

}
