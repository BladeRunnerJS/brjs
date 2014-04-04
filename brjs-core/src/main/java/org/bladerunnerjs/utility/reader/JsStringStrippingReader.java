package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;


public class JsStringStrippingReader extends AbstractStrippingReader
{
	
	public JsStringStrippingReader(Reader sourceReader)
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
		return new char[] { nextChar };
	}
	

}
