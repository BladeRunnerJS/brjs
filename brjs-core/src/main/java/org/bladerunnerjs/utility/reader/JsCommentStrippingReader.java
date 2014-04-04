package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;

public class JsCommentStrippingReader extends AbstractStrippingReader
{
	private enum CommentStripperState
	{
		WITHIN_SOURCE,
		WITHIN_SINGLY_QUOTED_STRING,
		WITHIN_DOUBLY_QUOTED_STRING,
		WITHIN_SINGLE_LINE_COMMENT,
		WITHIN_MULTI_LINE_COMMENT,
		WITHIN_JSDOC_COMMENT,
		FORWARD_SLASH_DETECTED,
		FORWARD_SLASH_ASTERISK_DETECTED
	}
	
	private static final int MAX_SINGLE_WRITE = 3;
	
	private final boolean preserveJsdoc;
	private CommentStripperState state;
	
	public JsCommentStrippingReader(Reader sourceReader, boolean preserveJsdoc)
	{
		super(sourceReader);
		this.preserveJsdoc = preserveJsdoc;
		state = CommentStripperState.WITHIN_SOURCE;
	}
	
	@Override
	protected int getMaxSingleWrite()
	{
		return MAX_SINGLE_WRITE;
	}
	
	protected char[] handleNextCharacter(char nextChar, char previousChar) throws IOException
	{
		StringBuffer newChars = new StringBuffer();
		
		switch(state)
		{
			case WITHIN_SOURCE:
				// Note: the previousChar check is a get out for not properly dealing with literal regular expressions
				if((nextChar == '/') && (previousChar != '\\'))
				{
					state = CommentStripperState.FORWARD_SLASH_DETECTED;
				}
				else if(nextChar == '\'')
				{
					state = CommentStripperState.WITHIN_SINGLY_QUOTED_STRING;
					newChars.append(nextChar);
				}
				else if(nextChar == '"')
				{
					state = CommentStripperState.WITHIN_DOUBLY_QUOTED_STRING;
					newChars.append(nextChar);
				}
				else
				{
					newChars.append(nextChar);
				}
				break;
			
			case WITHIN_SINGLY_QUOTED_STRING:
				newChars.append(nextChar);
				
				// Note: the new-line check is a get out for not properly dealing with literal regular expressions
				if(((nextChar == '\'') && (previousChar != '\\')) || (nextChar == '\n'))
				{
					state = CommentStripperState.WITHIN_SOURCE;
				}
				break;
			
			case WITHIN_DOUBLY_QUOTED_STRING:
				newChars.append(nextChar);
				
				// Note: the new-line check is a get out for not properly dealing with literal regular expressions
				if(((nextChar == '"') && (previousChar != '\\')) || (nextChar == '\n'))
				{
					state = CommentStripperState.WITHIN_SOURCE;
				}
				break;
			
			case FORWARD_SLASH_DETECTED:
				if(nextChar == '/')
				{
					state = CommentStripperState.WITHIN_SINGLE_LINE_COMMENT;
				}
				else if(nextChar == '*')
				{
					state = CommentStripperState.FORWARD_SLASH_ASTERISK_DETECTED;
				}
				else
				{
					state = CommentStripperState.WITHIN_SOURCE;
					newChars.append(previousChar);
					newChars.append(nextChar);
				}
				break;
			
			case FORWARD_SLASH_ASTERISK_DETECTED:
				if(nextChar == '*')
				{
					state = CommentStripperState.WITHIN_JSDOC_COMMENT;
					
					if(preserveJsdoc)
					{
						newChars.append("/**");
					}
				}
				else
				{
					state = CommentStripperState.WITHIN_MULTI_LINE_COMMENT;
				}
				
				break;
			
			case WITHIN_SINGLE_LINE_COMMENT:
				if((nextChar == '\r') || (nextChar == '\n'))
				{
					if(nextChar == '\n')
					{
						state = CommentStripperState.WITHIN_SOURCE;
					}
					
					newChars.append(nextChar);
				}
				break;
			
			case WITHIN_MULTI_LINE_COMMENT:
				if((nextChar == '/') && (previousChar == '*'))
				{
					state = CommentStripperState.WITHIN_SOURCE;
				}
				break;
			
			case WITHIN_JSDOC_COMMENT:
				if(preserveJsdoc)
				{
					newChars.append(nextChar);
				}
				
				if((nextChar == '/') && (previousChar == '*'))
				{
					state = CommentStripperState.WITHIN_SOURCE;
				}
				break;
		}
		
		return newChars.toString().toCharArray();
	}
	
}
