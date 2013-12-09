package org.bladerunnerjs.testing.specutility.engine;

import static org.junit.Assert.*;
import static org.bladerunnerjs.testing.utility.BRJSAssertions.*;

import java.util.ArrayList;
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
	
	public VerifierChainer containsText(String substring) {
		assertContains(substring, string);
		
		return verifierChainer;	
	}
	
	public VerifierChainer containsTextOnce(String substring) {
		assertEquals(1, StringUtils.countMatches(string, substring));
		
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

	public VerifierChainer textEquals(String content) {
		assertEquals(content, string);
		
		return verifierChainer;
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
}
