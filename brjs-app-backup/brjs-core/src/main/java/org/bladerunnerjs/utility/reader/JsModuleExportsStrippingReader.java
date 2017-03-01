package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.utility.FixedLengthStringBuilder;

/*
 * Note: This class has a lot of code that is duplicated with other comment stripping readers. 
 * DO NOT try to refactor them to share a single superclass, it leads to performance overheads that have a massive impact when bundling
 */

public class JsModuleExportsStrippingReader extends Reader {
	private String EXPORTS_MATCH = "exports=";
	private final Reader sourceReader;
	
	private final FixedLengthStringBuilder tailBuffer = new FixedLengthStringBuilder(EXPORTS_MATCH.length() + 10); // + 10 to allow for extra spaces in the definition
	
	private boolean moduleExportsLocated = false;
	
	private int nextCharPos = 0;
	private int lastCharPos = 0;
	private boolean stripPostModuleExports;
	private BRJS brjs;
	
	public JsModuleExportsStrippingReader(BRJS brjs, Reader sourceReader) {
		this(brjs, sourceReader, true);
	}
	
	public JsModuleExportsStrippingReader(BRJS brjs, Reader sourceReader, boolean stripPostModuleExports) {
		super();
		this.brjs = brjs;
		this.sourceReader = sourceReader;
		this.stripPostModuleExports = stripPostModuleExports;
	}
	
	@Override
	public int read(char[] destBuffer, int offset, int maxCharacters) throws IOException {
		if(lastCharPos == -1) {
			return -1;
		}
		
		int currentOffset = offset;
		int maxOffset = offset + maxCharacters;
		char nextChar;
		char[] sourceBuffer = CharBufferPool.getBuffer(brjs);
		
		while(currentOffset < maxOffset) {
			if (nextCharPos == lastCharPos) {
				nextCharPos = 0;
				lastCharPos = sourceReader.read(sourceBuffer, 0, sourceBuffer.length - 1);
				
				if(lastCharPos == -1) {
					break;
				}
			}
			
			nextChar = sourceBuffer[nextCharPos++];
			tailBuffer.append(nextChar);
			
			if (!moduleExportsLocated) {
				if (matchesModuleExports()) {
					moduleExportsLocated = true;
				}
			}
			
			if (moduleExportsLocated != stripPostModuleExports) {
				destBuffer[currentOffset++] = nextChar;
			}
		}
		
		CharBufferPool.returnBuffer(brjs, sourceBuffer);
		int charsProvided = (currentOffset - offset);
		return (charsProvided == 0) ? -1 : charsProvided;
	}
	
	@Override
	public void close() throws IOException {
		sourceReader.close();
	}
	
	private boolean matchesModuleExports() {
		// do not try to replace with with a regex that matches various module.exports permutations - using .contains is far more performant (see https://github.com/BladeRunnerJS/brjs/pull/1420)
		String condensedTailBufferContent = tailBuffer.toString().replaceAll("\\s+","");
		return condensedTailBufferContent.contains(EXPORTS_MATCH);
	}
	
}
