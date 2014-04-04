package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;


public class JsStringStrippingReader extends AbstractStrippingReader
{
	
	private enum CommentStripperState
	{
		WITHIN_SOURCE,
		WITHIN_SINGLY_QUOTED_STRING,
		WITHIN_DOUBLY_QUOTED_STRING
	}
	
	CommentStripperState state = CommentStripperState.WITHIN_SOURCE;
	
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
		StringBuffer newChars = new StringBuffer();
		
		switch(state)
		{
			case WITHIN_SOURCE:
				if (nextChar == '"')
				{
					state = CommentStripperState.WITHIN_DOUBLY_QUOTED_STRING;
				}
				else if (nextChar == '\'')
				{
					state = CommentStripperState.WITHIN_SINGLY_QUOTED_STRING;
				}
				else
				{
					newChars.append(nextChar);
				}
				break;
			case WITHIN_DOUBLY_QUOTED_STRING:
				if (nextChar == '"' || nextChar == '\n')
				{
					state = CommentStripperState.WITHIN_SOURCE;
				}
				break;
			case WITHIN_SINGLY_QUOTED_STRING:
				if (nextChar == '\'' || nextChar == '\n')
				{
					state = CommentStripperState.WITHIN_SOURCE;
				}
				break;
			
		}
		
		return newChars.toString().toCharArray();
	}
	

}
