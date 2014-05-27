package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;

/*
 * Note: This class has a lot of code that is duplicated with other comment stripping readers. 
 * DO NOT try to refactor them to share a single superclass, it leads to performance overheads that have a massive impact whe bundling
 */

/**
 * Strips the contents of XML comments when reading.
 * 
 * NOTE: This should only be used when determining dependencies of a class. 
 * 		It doesn't handle the removal of the start of the comment and will result in invalid XML.
 *
 */
public class XmlCommentStrippingDependenciesReader extends Reader
{
	public static final String COMMENT_START = "<!--";
	public static final String COMMENT_END = "-->";	
	private static final int MIN_BUFFERED_CHARS = COMMENT_START.length();
	
	private enum CommentStripperState
	{
		WITHIN_SOURCE,
		WITHIN_COMMENT,
		POSSIBLE_COMMENT_MATCH
	}
	
	private final Reader sourceReader;
	private final char[] sourceBuffer = new char[4096];
	private int nextCharPos = 0;
	private int lastCharPos = 0;
	private CommentStripperState state;
	
	public XmlCommentStrippingDependenciesReader(Reader sourceReader)
	{
		super();
		this.sourceReader = sourceReader;
		state = CommentStripperState.WITHIN_SOURCE;
	}
	
	@Override
	public int read(char[] destBuffer, int offset, int maxCharacters) throws IOException {
		if(lastCharPos == -1) {
			return -1;
		}
		
		int currentOffset = offset;
		int maxOffset = offset + maxCharacters;
		char nextChar;
		
		while(currentOffset < maxOffset) {
			if(nextCharPos == lastCharPos) {
				nextCharPos = 0;
				lastCharPos = sourceReader.read(sourceBuffer, 0, sourceBuffer.length - 1);
				
				if(lastCharPos == -1) {
					break;
				}
			}
			
			nextChar = sourceBuffer[nextCharPos++];
			
			if (state == CommentStripperState.WITHIN_SOURCE) {
				destBuffer[currentOffset++] = nextChar;
			}
			
			if (nextChar == COMMENT_START.charAt(COMMENT_START.length()-1)) {
				if (isCommentStart()) {
					state = CommentStripperState.WITHIN_COMMENT;
				}
			}
			else if (nextChar == COMMENT_END.charAt(COMMENT_END.length()-1)) {
				if (isCommentEnd()) {
					state = CommentStripperState.WITHIN_SOURCE;
				}
			}
		}
		
		int charsProvided = (currentOffset - offset);
		return (charsProvided == 0) ? -1 : charsProvided;
	}
	
	private boolean isCommentStart()
	{
		int startPos = Math.max(0, nextCharPos - MIN_BUFFERED_CHARS);
		String commentPrefix = new String(sourceBuffer, startPos, nextCharPos - startPos);
		return commentPrefix.contains(COMMENT_START);
	}
	
	private boolean isCommentEnd()
	{
		int startPos = Math.max(0, nextCharPos - MIN_BUFFERED_CHARS);
		String commentPrefix = new String(sourceBuffer, startPos, nextCharPos - startPos);
		return commentPrefix.contains(COMMENT_END);
	}

	@Override
	public void close() throws IOException {
		sourceReader.close();
	}
}
