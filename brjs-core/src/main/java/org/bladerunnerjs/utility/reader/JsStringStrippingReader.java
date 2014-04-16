package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;


public class JsStringStrippingReader extends Reader
{
	private enum StringStripperState
	{
		WITHIN_SOURCE,
		WITHIN_SINGLY_QUOTED_STRING,
		WITHIN_DOUBLY_QUOTED_STRING
	}
	
	private final Reader sourceReader;
	private final char[] sourceBuffer = new char[4096];
	private int nextCharPos = 0;
	private int lastCharPos = 0;
	private StringStripperState state = StringStripperState.WITHIN_SOURCE;
	
	public JsStringStrippingReader(Reader sourceReader)
	{
		super();
		this.sourceReader = sourceReader;
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
			
			switch(state)
			{
				case WITHIN_SOURCE:
					if (nextChar == '"')
					{
						state = StringStripperState.WITHIN_DOUBLY_QUOTED_STRING;
					}
					else if (nextChar == '\'')
					{
						state = StringStripperState.WITHIN_SINGLY_QUOTED_STRING;
					}
					else
					{
						destBuffer[currentOffset++] = nextChar;
					}
					break;
				case WITHIN_DOUBLY_QUOTED_STRING:
					if (nextChar == '"' || nextChar == '\n')
					{
						state = StringStripperState.WITHIN_SOURCE;
					}
					break;
				case WITHIN_SINGLY_QUOTED_STRING:
					if (nextChar == '\'' || nextChar == '\n')
					{
						state = StringStripperState.WITHIN_SOURCE;
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
