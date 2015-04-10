package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.utility.TailBuffer;

/*
 * Note: This class has a lot of code that is duplicated with other comment stripping readers. 
 * DO NOT try to refactor them to share a single superclass, it leads to performance overheads that have a massive impact whe bundling
 */

public class JsModuleExportsStrippingReader extends Reader {
	public static final String MODULE_EXPORTS_REGEX = "^.*(module\\.)?exports\\W*=.*";
	public static final Pattern MODULE_EXPORTS_REGEX_PATTERN = Pattern.compile(MODULE_EXPORTS_REGEX, Pattern.DOTALL);

	private final Reader sourceReader;
	
	private final TailBuffer tailBuffer = new TailBuffer(MODULE_EXPORTS_REGEX.length() + 10); // + 10 to allow for extra spaces in the definition
	
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
			tailBuffer.push(nextChar);
			
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
		String tail = new String(tailBuffer.toArray());
		Matcher moduleExportsMatcher = MODULE_EXPORTS_REGEX_PATTERN.matcher(tail);
		
		return moduleExportsMatcher.matches();
	}
	
}
