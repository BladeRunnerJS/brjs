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
		this.string = stringBuffer.toString();
		this.verifierChainer = new VerifierChainer(specTest);
	}
	
	public VerifierChainer containsText(String... substrings) {
		for(String substring : substrings)
		{
			assertContains(substring, string);
		}
		
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
			containsText(className + " = function()");
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
		int i = 0;
		
		assertEquals("'" + string + "' does not contain exactly " + expectedRequests.length + " request(s)", expectedRequests.length, actualRequests.size());
		
		for(String expectedRequest : expectedRequests) {
			String actualRequest = actualRequests.get(i++);
			
			assertEquals(expectedRequest, actualRequest);
		}
		
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
}
