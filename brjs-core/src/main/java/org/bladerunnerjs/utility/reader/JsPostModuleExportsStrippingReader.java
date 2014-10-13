package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;

/*
 * Note: This class has a lot of code that is duplicated with other comment stripping readers. 
 * DO NOT try to refactor them to share a single superclass, it leads to performance overheads that have a massive impact whe bundling
 */

public class JsPostModuleExportsStrippingReader extends Reader {
	private final Reader sourceReader;
	private final CharBufferPool pool;
	
	private String matchStr = "module.exports =";
	private int matchPos = 0;
	private boolean moduleExportsLocated = false;
	
	private int nextCharPos = 0;
	private int lastCharPos = 0;
	
	public JsPostModuleExportsStrippingReader(Reader sourceReader, CharBufferPool pool) {
		super();
		this.sourceReader = sourceReader;
		this.pool = pool;
	}
	
	@Override
	public int read(char[] destBuffer, int offset, int maxCharacters) throws IOException {
		if(lastCharPos == -1) {
			return -1;
		}
		
		int currentOffset = offset;
		int maxOffset = offset + maxCharacters;
		char nextChar;
		char[] sourceBuffer = pool.getBuffer();
		
		while(currentOffset < maxOffset) {
			if (nextCharPos == lastCharPos) {
				nextCharPos = 0;
				lastCharPos = sourceReader.read(sourceBuffer, 0, sourceBuffer.length - 1);
				
				if(lastCharPos == -1) {
					break;
				}
			}
			
			nextChar = sourceBuffer[nextCharPos++];
			
			if(!moduleExportsLocated) {
				if(nextChar == matchStr.charAt(matchPos)) {
					++matchPos;
					
					if(matchPos == (matchStr.length() - 1)) {
						moduleExportsLocated = true;
					}
				}
				else {
					matchPos = 0;
				}
				
				destBuffer[currentOffset++] = nextChar;
			}
		}
		
		pool.returnBuffer(sourceBuffer);
		int charsProvided = (currentOffset - offset);
		return (charsProvided == 0) ? -1 : charsProvided;
	}
	
	@Override
	public void close() throws IOException {
		sourceReader.close();
	}
}
