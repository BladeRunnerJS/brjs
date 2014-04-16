package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JsCodeBlockStrippingReader extends Reader
{
	private static final String SELF_EXECUTING_FUNCTION_DEFINITION_REGEX = "^.*([\\(\\!\\~\\-\\+]|(new\\s+))function\\s*\\([^)]*\\)\\s*\\{$";
	private static final Pattern SELF_EXECUTING_FUNCTION_DEFINITION_REGEX_PATTERN = Pattern.compile(SELF_EXECUTING_FUNCTION_DEFINITION_REGEX, Pattern.DOTALL);
	private static final int MIN_BUFFERED_CHARS = SELF_EXECUTING_FUNCTION_DEFINITION_REGEX.length(); // buffer the length of the function definition
	
	private final Reader sourceReader;
	private final char[] sourceBuffer = new char[4096];
	private int nextCharPos = 0;
	private int lastCharPos = 0;
	private int depthCount = 0;
	
	public JsCodeBlockStrippingReader(Reader sourceReader) {
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
			
			if(depthCount == 0) {
				destBuffer[currentOffset++] = nextChar;
			}
			
			if(nextChar == '{') {
				if((depthCount > 0) || (!isImmediatelyInvokingFunction())) {
					++depthCount;
				}
			}
			else if(nextChar == '}') {
				if(depthCount > 0) {
					--depthCount;
					
					if(depthCount == 0) {
						destBuffer[currentOffset++] = nextChar;
					}
				}
			}
		}
		
		int charsProvided = (currentOffset - offset);
		return (charsProvided == 0) ? -1 : charsProvided;
	}
	
	@Override
	public void close() throws IOException {
		sourceReader.close();
	}
	
	private boolean isImmediatelyInvokingFunction() {
		int startPos = Math.max(0, nextCharPos - MIN_BUFFERED_CHARS);
		String functionPrefix = new String(sourceBuffer, startPos, nextCharPos - startPos);
		Matcher immedidatelyInvokingFunctionMatcher = SELF_EXECUTING_FUNCTION_DEFINITION_REGEX_PATTERN.matcher(functionPrefix);
		
		return immedidatelyInvokingFunctionMatcher.matches();
	}
}
