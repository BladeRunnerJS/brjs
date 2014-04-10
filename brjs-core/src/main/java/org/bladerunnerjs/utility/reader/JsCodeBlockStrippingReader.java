package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JsCodeBlockStrippingReader extends AbstractStrippingReader
{
	private static final String SELF_EXECUTING_FUNCTION_DEFINITION_REGEX = "([\\(\\!\\~\\-\\+]|(new\\s+))function\\s*\\([^\\)]*\\)\\s*\\{";
	private static final Pattern SELF_EXECUTING_FUNCTION_DEFINITION_REGEX_PATTERN = Pattern.compile(SELF_EXECUTING_FUNCTION_DEFINITION_REGEX);

	private static final int MIN_BUFFERED_CHARS = SELF_EXECUTING_FUNCTION_DEFINITION_REGEX.length(); // buffer the length of the function definition
	
	private StringBuffer charBuffer = new StringBuffer();
	int depthCount = 0;

	public JsCodeBlockStrippingReader(Reader sourceReader)
	{
		super(sourceReader);
	}

	@Override
	protected int getMaxSingleWrite()
	{
		return 1;
	}

	@Override
	protected char[] handleNextCharacter(char nextChar, char previousChar) throws IOException
	{
		charBuffer.append(nextChar);
		
		if (charBuffer.length() < MIN_BUFFERED_CHARS)
		{
			return "".toCharArray();
		}
		
		return handleNextBufferedCharacter();
	}

	@Override
	protected char[] flush() throws IOException
	{
		StringBuffer flushedChars = new StringBuffer();
		
		while (charBuffer.length() > 0)
		{
			flushedChars.append( handleNextBufferedCharacter() );
		}
		
		return flushedChars.toString().toCharArray();
	}
	
	private char[] handleNextBufferedCharacter() throws IOException
	{
		Matcher selfExecFunctionMatcher = SELF_EXECUTING_FUNCTION_DEFINITION_REGEX_PATTERN.matcher( charBuffer.toString() );
		if ( selfExecFunctionMatcher.find() )
		{
			charBuffer = new StringBuffer( selfExecFunctionMatcher.replaceAll("}") );
			depthCount--;
		}
		
		char nextBufferedChar = charBuffer.charAt(0);
		charBuffer.deleteCharAt(0);
		return handleNextCharacter( nextBufferedChar );
	}
	
	private char[] handleNextCharacter(char processChar) throws IOException
	{
		if (processChar == '{')
		{
			depthCount++;
		}
		else if (processChar == '}')
		{
			depthCount = Math.max( 0, --depthCount );
		}
		else
		{
			if (depthCount <= 0)
			{
				return new char[] { processChar };			
			}
		}
		
		return "".toCharArray();
	}
	
}
