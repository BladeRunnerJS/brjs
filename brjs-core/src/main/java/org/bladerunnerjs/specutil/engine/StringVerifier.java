package org.bladerunnerjs.specutil.engine;

import static org.junit.Assert.*;

public class StringVerifier {
	private String string;
	private VerifierChainer verifierChainer;
	
	public StringVerifier(SpecTest specTest, StringBuffer stringBuffer) {
		this.string = stringBuffer.toString();
		this.verifierChainer = new VerifierChainer(specTest);
	}
	
	public VerifierChainer containsText(String substring) {
		if(!string.contains(substring)) {
			assertEquals(substring, string);
		}
		
		return verifierChainer;		
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
	
	public VerifierChainer containsRequests(String... requests) {
		for(String request : requests) {
			containsText(request);
		}
		
		int requestCount = string.split("<script").length - 1;
		assertEquals("'" + string + "' does not contain exactly " + requests.length + " request(s)", requests.length, requestCount);
		
		return verifierChainer;
	}
	
	public VerifierChainer isEmpty() {
		assertEquals("", string.trim());
		
		return verifierChainer;
	}
}
