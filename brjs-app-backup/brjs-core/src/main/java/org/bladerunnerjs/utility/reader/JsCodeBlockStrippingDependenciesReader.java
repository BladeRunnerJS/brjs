package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.utility.FixedLengthStringBuilder;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

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
	public static final Pattern SELF_EXECUTING_FUNCTION_DEFINITION_REGEX_PATTERN = Pattern.compile(SELF_EXECUTING_FUNCTION_DEFINITION_REGEX, Pattern.DOTALL);
	
	private static final String INLINE_MAP_DEFINITION_REGEX = "[a-zA-Z][\\w]+[\\s]+=[\\s]+\\{";
	private static final Pattern INLINE_MAP_DEFINITION_REGEX_PATTERN = Pattern.compile(INLINE_MAP_DEFINITION_REGEX);
	
	private static final Predicate<String> DEFAULT_FOUND_MOBULE_EXPORTS_PREDICATE = Predicates.alwaysFalse();
	
	private final Reader sourceReader;
	// buffer the length of the function definition + 12 to allow for things like new(<IIFE>) etc.
	private final FixedLengthStringBuilder tailBuffer = new FixedLengthStringBuilder(SELF_EXECUTING_FUNCTION_DEFINITION_REGEX.length() + 12);
	private int nextCharPos = 0;
	private int lastCharPos = 0;
	private int depthCount = 0;
	private Predicate<Integer> matcherPredicate;
	private Predicate<String> foundModuleExportsPredicate;
	private BRJS brjs;
	
	
	public JsCodeBlockStrippingDependenciesReader(BRJS brjs, Reader sourceReader) {
		this(brjs, sourceReader, new LessThanPredicate(1), DEFAULT_FOUND_MOBULE_EXPORTS_PREDICATE);
	}
	
	public JsCodeBlockStrippingDependenciesReader(BRJS brjs, Reader sourceReader, Predicate<Integer> matcherPredicate) {
		this(brjs, sourceReader, matcherPredicate, DEFAULT_FOUND_MOBULE_EXPORTS_PREDICATE);
	}
	
	public JsCodeBlockStrippingDependenciesReader(BRJS brjs, Reader sourceReader, Predicate<Integer> matcherPredicate, Predicate<String> foundModuleExportsPredicate) {
		super();
		this.brjs = brjs;
		this.sourceReader = sourceReader;
		this.matcherPredicate = matcherPredicate;
		this.foundModuleExportsPredicate = foundModuleExportsPredicate;
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
			
			boolean writtenChar = false;
			nextChar = sourceBuffer[nextCharPos++];
			tailBuffer.append(nextChar);
			
			if (satisfiesMatcherPredicateAndModuleExportsPredicate()) {
				destBuffer[currentOffset++] = nextChar;
				writtenChar = true;
			}
			
			if (nextChar == '{') {
				if ((depthCount > 0) || (!isImmediatelyInvokingFunction() && !isInlineMapDefiniton())) {
					++depthCount;
				}
			}
			else if (nextChar == '}') {
				if (depthCount > 0) {
					--depthCount;
					
					if (satisfiesMatcherPredicateAndModuleExportsPredicate() && !writtenChar) {
						destBuffer[currentOffset++] = nextChar;
					}
				}
			}
		}
		
		CharBufferPool.returnBuffer(brjs, sourceBuffer);
		int charsProvided = (currentOffset - offset);
		return (charsProvided == 0) ? -1 : charsProvided;
	}

	public boolean satisfiesMatcherPredicateAndModuleExportsPredicate()
	{
		return matcherPredicate.apply(depthCount) || foundModuleExportsPredicate.apply(tailBuffer.toString());
	}
	
	@Override
	public void close() throws IOException {
		sourceReader.close();
	}
	
	private boolean isImmediatelyInvokingFunction() {
		Matcher immedidatelyInvokingFunctionMatcher = SELF_EXECUTING_FUNCTION_DEFINITION_REGEX_PATTERN.matcher(tailBuffer.toString());
		
		return immedidatelyInvokingFunctionMatcher.matches();
	}
	
	private boolean isInlineMapDefiniton() {
		Matcher inlineMapDefinitionMatcher = INLINE_MAP_DEFINITION_REGEX_PATTERN.matcher( tailBuffer.toString() );
		return inlineMapDefinitionMatcher.find();
	}
	
	
	public static class LessThanPredicate implements Predicate<Integer> {
		private int value;
		public LessThanPredicate(int value) {
			this.value = value;
		}
		@Override
		public boolean apply(Integer input)
		{
			return input < value;
		}
	}
	
	public static class MoreThanPredicate implements Predicate<Integer> {
		private int value;
		public MoreThanPredicate(int value) {
			this.value = value;
		}
		@Override
		public boolean apply(Integer input)
		{
			return input > value;
		}
	}
	
}
