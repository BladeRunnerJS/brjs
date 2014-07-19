package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.utility.TailBuffer;

/*
 * Note: This class has a lot of code that is duplicated with other comment stripping readers. 
 * DO NOT try to refactor them to share a single superclass, it leads to performance overheads that have a massive impact whe bundling
 */

/**
 * Strips the contents of function blocks when reading.
 * 
 * NOTE: This should only be used when determining the dependencies of a class. 
 * 		It doesn't handle the removal of the start of the function block and will result in invalid JS.
 *
 */
public class JsCodeBlockStrippingDependenciesReader extends Reader
{
	private static final String SELF_EXECUTING_FUNCTION_DEFINITION_REGEX = "^.*([\\(\\!\\~\\-\\+]|(new\\s+))function\\s*\\([^)]*\\)\\s*\\{";
	private static final Pattern SELF_EXECUTING_FUNCTION_DEFINITION_REGEX_PATTERN = Pattern.compile(SELF_EXECUTING_FUNCTION_DEFINITION_REGEX, Pattern.DOTALL);
	
	private static final String INLINE_MAP_DEFINITION_REGEX = "[a-zA-Z][\\w]+[\\s]+=[\\s]+\\{";
	private static final Pattern INLINE_MAP_DEFINITION_REGEX_PATTERN = Pattern.compile(INLINE_MAP_DEFINITION_REGEX);
	
	private final Reader sourceReader;
	// buffer the length of the function definition + 10 to allow for things like new(<IIFE>) etc.
	private final TailBuffer tailBuffer = new TailBuffer(SELF_EXECUTING_FUNCTION_DEFINITION_REGEX.length() + 10 + 1);
	private int nextCharPos = 0;
	private int lastCharPos = 0;
	private int depthCount = 0;
	
	
	public JsCodeBlockStrippingDependenciesReader(Reader sourceReader) {
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
		char[] sourceBuffer = CharBufferPool.getBuffer();
		
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
			
			if (depthCount == 0) {
				destBuffer[currentOffset++] = nextChar;
			}
			
			if (nextChar == '{') {
				if ((depthCount > 0) || (!isImmediatelyInvokingFunction() && !isInlineMapDefiniton())) {
					++depthCount;
				}
			}
			else if (nextChar == '}') {
				if (depthCount > 0) {
					--depthCount;
					
					if (depthCount == 0) {
						destBuffer[currentOffset++] = nextChar;
					}
				}
			}
		}
		
		CharBufferPool.returnBuffer(sourceBuffer);
		int charsProvided = (currentOffset - offset);
		return (charsProvided == 0) ? -1 : charsProvided;
	}
	
	@Override
	public void close() throws IOException {
		sourceReader.close();
	}
	
	private boolean isImmediatelyInvokingFunction() {
		String tail = new String(tailBuffer.toArray());
		Matcher immedidatelyInvokingFunctionMatcher = SELF_EXECUTING_FUNCTION_DEFINITION_REGEX_PATTERN.matcher(tail);
		
		return immedidatelyInvokingFunctionMatcher.matches();
	}
	
	private boolean isInlineMapDefiniton() {
		String tail = new String(tailBuffer.toArray());
		Matcher inlineMapDefinitionMatcher = INLINE_MAP_DEFINITION_REGEX_PATTERN.matcher( tail );
		return inlineMapDefinitionMatcher.find();
	}
}
