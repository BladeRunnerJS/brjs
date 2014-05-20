package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;

/*
 * Note: This class has a lot of code that is duplicated with other comment stripping readers. 
 * DO NOT try to refactor them to share a single superclass, it leads to performance overheads that have a massive impact whe bundling
 */
public class JsCommentStrippingReader extends Reader
{
	private static final int MAX_SINGLE_WRITE = 3;
	
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
	
	private final Reader sourceReader;
	private final boolean preserveJsdoc;
	private final char[] sourceBuffer = new char[4096];
	private int nextCharPos = 0;
	private int lastCharPos = 0;
	private CommentStripperState state;
	
	public JsCommentStrippingReader(Reader sourceReader, boolean preserveJsdoc)
	{
		super();
		this.sourceReader = sourceReader;
		this.preserveJsdoc = preserveJsdoc;
		state = CommentStripperState.WITHIN_SOURCE;
	}
	
	@Override
	public int read(char[] destBuffer, int offset, int maxCharacters) throws IOException {
		if(lastCharPos == -1) {
			return -1;
		}
		
		int currentOffset = offset;
		int maxOffset = offset + maxCharacters - (MAX_SINGLE_WRITE - 1);
		char previousChar, nextChar = '\0';
		
		while(currentOffset < maxOffset) {
			if(nextCharPos == lastCharPos) {
				nextCharPos = 0;
				lastCharPos = sourceReader.read(sourceBuffer, 0, sourceBuffer.length - 1);
				
				if(lastCharPos == -1) {
					break;
				}
			}
			
			previousChar = nextChar;
			nextChar = sourceBuffer[nextCharPos++];
			
			if (previousChar == '\r' && nextChar != '\n') {
				throw new IOException("Mac line endings detected. This type of line ending is not supported.");
			}
			
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
						destBuffer[currentOffset++] = nextChar;
					}
					else if(nextChar == '"')
					{
						state = CommentStripperState.WITHIN_DOUBLY_QUOTED_STRING;
						destBuffer[currentOffset++] = nextChar;
					}
					else
					{
						destBuffer[currentOffset++] = nextChar;
					}
					break;
				
				case WITHIN_SINGLY_QUOTED_STRING:
					destBuffer[currentOffset++] = nextChar;
					
					// Note: the new-line check is a get out for not properly dealing with literal regular expressions
					if(((nextChar == '\'') && (previousChar != '\\')) || (nextChar == '\n'))
					{
						state = CommentStripperState.WITHIN_SOURCE;
					}
					break;
				
				case WITHIN_DOUBLY_QUOTED_STRING:
					destBuffer[currentOffset++] = nextChar;
					
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
						destBuffer[currentOffset++] = previousChar;
						destBuffer[currentOffset++] = nextChar;
					}
					break;
				
				case FORWARD_SLASH_ASTERISK_DETECTED:
					if(nextChar == '*')
					{
						state = CommentStripperState.WITHIN_JSDOC_COMMENT;
						
						if(preserveJsdoc)
						{
							destBuffer[currentOffset++] = '/';
							destBuffer[currentOffset++] = '*';
							destBuffer[currentOffset++] = '*';
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
						
						destBuffer[currentOffset++] = nextChar;
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
						destBuffer[currentOffset++] = nextChar;
					}
					
					if((nextChar == '/') && (previousChar == '*'))
					{
						state = CommentStripperState.WITHIN_SOURCE;
					}
					break;
			}
		}
		
		int charsProvided = (currentOffset - offset);
		return (charsProvided == 0) ? -1 : charsProvided;
	}
	
	@Override
	public void close() throws IOException {
		sourceReader.close();
	}
}
