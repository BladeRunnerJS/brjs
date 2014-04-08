package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;


public class JsCodeBlockStrippingReader extends AbstractStrippingReader
{
	private static final char[] SELF_EXECUTING_FUNCTION_DEFINITION = "(function()".toCharArray();
	
	private char[] emptyChars = new char[0];
	int charCount = 0;
	int nestedCodeBlockDepth = 0;
	int depthCount = 0;
	boolean possibleSelfExecutingFunction = true;

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
		// handle self executing functions prefixed by a ;
		if (charCount == 0 && nextChar == ';')
		{
			return emptyChars;
		}
		
		if ( possibleSelfExecutingFunction && SELF_EXECUTING_FUNCTION_DEFINITION[charCount] == nextChar )
		{
			possibleSelfExecutingFunction = true;
			if ( (charCount+1) == SELF_EXECUTING_FUNCTION_DEFINITION.length)
			{
				nestedCodeBlockDepth++;
				possibleSelfExecutingFunction = false;
			}
		}
		else
		{
			possibleSelfExecutingFunction = false;
		}
		charCount++;
		
		if (nextChar == '{')
		{
			depthCount++;
		}
		else if (nextChar == '}')
		{
			depthCount = Math.max( nestedCodeBlockDepth, --depthCount );
		}
		else
		{
			if (depthCount <= nestedCodeBlockDepth)
			{
				return new char[] { nextChar };			
			}
		}
		
		return emptyChars;
	}

}
