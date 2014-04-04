package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;


public class JsCodeBlockStrippingReader extends AbstractStrippingReader
{
	private char[] emptyChars = new char[0];
	int depthCount = 0;

	public JsCodeBlockStrippingReader(Reader sourceReader)
	{
		super(sourceReader);
	}

	@Override
	int getMaxSingleWrite()
	{
		return 1;
	}

	@Override
	protected char[] handleNextCharacter(char nextChar, char previousChar) throws IOException
	{
		if (nextChar == '{')
		{
			depthCount++;
		}
		else if (nextChar == '}')
		{
			depthCount = Math.max(0, depthCount--);
		}
		
		if (depthCount == 0)
		{
			return new char[] { nextChar };			
		}
		return emptyChars;
	}

}
