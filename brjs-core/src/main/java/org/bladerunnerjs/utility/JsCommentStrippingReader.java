package org.bladerunnerjs.utility;

import java.io.IOException;
import java.io.Reader;

public class JsCommentStrippingReader extends Reader
{
	private static final int MAX_SINGLE_WRITE = 3;
	
	private final Reader sourceReader;
	private final boolean preserveJsdoc;
	private CommentStripperState state;
	private char previousChar;
	private StringBuffer overflowBuffer = new StringBuffer();
	
	private static final String LINE_SEP = System.getProperty("line.separator");
	
	public JsCommentStrippingReader(Reader sourceReader, boolean preserveJsdoc)
	{
		this.sourceReader = sourceReader;
		this.preserveJsdoc = preserveJsdoc;
		state = CommentStripperState.WITHIN_SOURCE;
	}
	
	@Override
	public void close() throws IOException
	{
		sourceReader.close();
	}
	
	@Override
	public int read(char[] buff, int offset, int maxCharacters) throws IOException
	{
		int charactersWritten = 0;
		int maxEfficientCharacters = Math.max(1, maxCharacters - MAX_SINGLE_WRITE + 1);
		int nextInt;
		
		if(overflowBuffer.length() > 0)
		{
			String overflowString = overflowBuffer.toString();
			overflowBuffer.setLength(0);
			charactersWritten = write(overflowString, buff, offset, maxCharacters, charactersWritten);
		}
		
		while((charactersWritten < maxEfficientCharacters) && ((nextInt = sourceReader.read()) != -1))
		{
			char nextChar = (char) nextInt;
			
			ensureCharactersDontMatchMacLineEndings(previousChar, nextChar);
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
						charactersWritten = write(nextChar, buff, offset, maxCharacters, charactersWritten);
					}
					else if(nextChar == '"')
					{
						state = CommentStripperState.WITHIN_DOUBLY_QUOTED_STRING;
						charactersWritten = write(nextChar, buff, offset, maxCharacters, charactersWritten);
					}
					else
					{
						charactersWritten = write(nextChar, buff, offset, maxCharacters, charactersWritten);
					}
					break;
				
				case WITHIN_SINGLY_QUOTED_STRING:
					charactersWritten = write(nextChar, buff, offset, maxCharacters, charactersWritten);
					
					// Note: the new-line check is a get out for not properly dealing with literal regular expressions
					if(((nextChar == '\'') && (previousChar != '\\')) || (nextChar == '\n'))
					{
						state = CommentStripperState.WITHIN_SOURCE;
					}
					break;
				
				case WITHIN_DOUBLY_QUOTED_STRING:
					charactersWritten = write(nextChar, buff, offset, maxCharacters, charactersWritten);
					
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
						charactersWritten = write(previousChar, buff, offset, maxCharacters, charactersWritten);
						charactersWritten = write(nextChar, buff, offset, maxCharacters, charactersWritten);
					}
					break;
				
				case FORWARD_SLASH_ASTERISK_DETECTED:
					if(nextChar == '*')
					{
						state = CommentStripperState.WITHIN_JSDOC_COMMENT;
						
						if(preserveJsdoc)
						{
							charactersWritten = write("/**", buff, offset, maxCharacters, charactersWritten);
						}
					}
					else
					{
						state = CommentStripperState.WITHIN_MULTI_LINE_COMMENT;
					}
					
					break;
				
				case WITHIN_SINGLE_LINE_COMMENT:
					if(nextChar == '\n')
					{
						state = CommentStripperState.WITHIN_SOURCE;
						charactersWritten = write(LINE_SEP, buff, offset, maxCharacters, charactersWritten);
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
						charactersWritten = write(nextChar, buff, offset, maxCharacters, charactersWritten);
					}
					
					if((nextChar == '/') && (previousChar == '*'))
					{
						state = CommentStripperState.WITHIN_SOURCE;
					}
					break;
			}
			
			previousChar = nextChar;
		}
		
		return (charactersWritten == 0) ? -1 : charactersWritten;
	}
	
	private int write(String characters, char[] buff, int offset, int maxCharacters, int charactersWritten)
	{
		for(int i = 0, l = characters.length(); i < l; ++i)
		{
			charactersWritten = write(characters.charAt(i), buff, offset, maxCharacters, charactersWritten);
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
	
	public void ensureCharactersDontMatchMacLineEndings(char previousChar, char nextChar) throws IOException
	{
		if (previousChar == '\r' && nextChar != '\n')
		{
			throw new IOException("Mac line endings detected. This type of line ending is not supported.");
		}
	}
	
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
}
