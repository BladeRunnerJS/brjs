package org.bladerunnerjs.testing.specutility.engine;

import static org.junit.Assert.*;
import static org.bladerunnerjs.testing.utility.BRJSAssertions.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;

public class StringVerifier {
	private static final Pattern scriptPattern = Pattern.compile("<script type='text/javascript' src='([^']+)'>");
	
	private String string;
	private VerifierChainer verifierChainer;
	
	public StringVerifier(SpecTest specTest, StringBuffer stringBuffer) {
		this(specTest, stringBuffer.toString());
	}
	
	public StringVerifier(SpecTest specTest, String string) {
		this.string = string;
		this.verifierChainer = new VerifierChainer(specTest);
	}
	
	public VerifierChainer containsText(String substring) {
		assertContains(substring, string);
		
		return verifierChainer;
	}
	
	public VerifierChainer containsTextANumberOfTimes(String substring, int numberOfTimes)
	{
		if (StringUtils.countMatches(string, substring) != numberOfTimes) {
			assertEquals(substring, string);
		}
		
		return verifierChainer;	
	}
	
	public VerifierChainer containsTextOnce(String substring) {
		containsTextANumberOfTimes(substring, 1);
		
		return verifierChainer;	
	}
	
	public VerifierChainer containsLines(String... lines) {
		return containsText(Joiner.on("\n").join(lines));
	}
	
	public VerifierChainer containsClasses(String... classes) {
		for(String className : classes) {
			containsText(className + " = function() {");
		}
		
		return verifierChainer;
	}
	
	// TODO: this method needs fixing as it currently allows you to type an require prefix, and as long the class name at the end exists it works
	public VerifierChainer containsCommonJsClasses(String... classes) {
		List<String> processedNodeClassNames = new ArrayList<String>();
		for(String className : classes) {
			className = className.replaceAll("\\.", "/");
			String commonJsClassName = StringUtils.substringAfterLast(className, "/");
			if (processedNodeClassNames.contains(commonJsClassName)) {
				throw new RuntimeException("CommonJS classes must not have the same suffix names as it leads to false positives. e.g. some.Name and another.Name should be some.Name and some.otherName");
			}
			containsText(commonJsClassName + " = function() {\n");
			containsText("exports = " + commonJsClassName);
			processedNodeClassNames.add(commonJsClassName);
		}
		
		return verifierChainer;
	}
	
	public VerifierChainer containsDefinedClasses(String... classRequirePaths)
	{
		for(String classRequirePath : classRequirePaths) {
			String expectedDefineString = "define('" + classRequirePath + "', ";
			
			if(!string.contains(expectedDefineString) )
			{
				assertEquals("could not find expected string inside response", expectedDefineString, string);
			}
		}
		
		return verifierChainer;
	}

	public VerifierChainer textEquals(String content) {
		assertEquals(content, string);
		
		return verifierChainer;
	}
	
	public VerifierChainer textEquals(StringBuffer content) {
		return textEquals(content.toString());
	}
	
	public VerifierChainer containsRequests(String... expectedRequests) {
		List<String> actualRequests = getRequestPaths(string);
		assertEquals(Joiner.on(", ").join(expectedRequests), Joiner.on(", ").join(actualRequests));
		
		return verifierChainer;
	}
	
	private List<String> getRequestPaths(String content) {
		Matcher matcher = scriptPattern.matcher(content);
		List<String> requestPaths = new ArrayList<>();
		
		while (matcher.find()) {
			requestPaths.add(matcher.group(1));
		}
		
		return requestPaths;
	}
	
	public VerifierChainer isEmpty() {
		assertEquals("", string.trim());
		
		return verifierChainer;
	}
	
	public VerifierChainer isNotEmpty() {
		if(string.trim().length() == 0) {
			assertEquals("", string.trim());
		}
		
		return verifierChainer;
	}

	public VerifierChainer containsMinifiedClasses(String... classes)
	{
		for(String className : classes) {
			containsText(className + "=function()");
		}
		
		return verifierChainer;
	}

	public VerifierChainer doesNotContainClasses(String... classes) 
	{
		for(String className : classes) {
			doesNotContainText(className + " = function()");
			String commonJsClassName = className.replaceAll("\\.", "/");
			commonJsClassName = StringUtils.substringAfterLast(commonJsClassName, "/");
			doesNotContainText(commonJsClassName + " = function()");
		}
		
		return verifierChainer;
	}

	public VerifierChainer doesNotContainText(String substring) {
		assertDoesNotContain(substring, string);
		
		return verifierChainer;	
	}
	
	public VerifierChainer doesNotEndWithTextExcludingWhitespace(String substring) {
		assertFalse(string.trim().endsWith(substring));
		
		return verifierChainer;	
	}

	public VerifierChainer containsOrderedTextFragments(String... textFragments) {
		
		containsOrderedTextFragmentsAnyNumberOfTimes(textFragments);
		
		for (String fragment : textFragments) {
			if (StringUtils.countMatches(string, fragment) != 1) {
				String failMessage = "Expected " + fragment + " to be present only once. "+
						"If fragments can be present multiple times use 'containsOrderedTextFragmentsAnyNumberOfTimes' instaed";
				assertEquals(failMessage, string, fragment);
			}
		}
		
		return verifierChainer;
	}
	
	public VerifierChainer containsOrderedTextFragmentsAnyNumberOfTimes(String... textFragments) {
		if(textFragments.length == 0) {
			throw new RuntimeException("containsOrderedTextFragments() invoked without arguments.");
		}
		else if(textFragments.length == 1) {
			throw new RuntimeException("containsOrderedTextFragments() should only be used when there is more than one text fragment; please use containsText() instead.");
		}
		
		List<String> escapedTextFragments = new LinkedList<String>();
		for (String fragment : textFragments)
		{
			escapedTextFragments.add( Pattern.quote(fragment) );
		}
		
		if(!string.matches("(?s)^.*" + Joiner.on(".*").join(escapedTextFragments) + ".*$")) {
			assertEquals(Joiner.on("\n<snip/>\n").join(escapedTextFragments), string);
		}
		
		return verifierChainer;
	}
	
	/* override equals so we don't get tests that give false positives when someone accidentally uses the 'equals' method instead of 'textEquals' */
	@Override
	public boolean equals(Object o)
	{
		System.err.println("WARNING: 'textEquals()' should be used in SpecTests rather than 'equals()'.");
		textEquals((String) o);
		return true;
	}
	
	@Override /* override this to prevent compiler warnings */
	public int hashCode()
	{
		return string.hashCode();
	}
	
}
