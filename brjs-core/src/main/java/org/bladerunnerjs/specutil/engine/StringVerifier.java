package org.bladerunnerjs.specutil.engine;

import static org.junit.Assert.*;

public class StringVerifier {
	private String string;
	
	public StringVerifier(SpecTest specTest, StringBuffer stringBuffer) {
		this.string = stringBuffer.toString();
	}
	
	public void containsText(String substring) {
		if(!string.contains(substring)) {
			assertEquals(substring, string);
		}
		
	}
	
	public void containsClasses(String... classes) {
		for(String className : classes) {
			containsText(className + " = function()");
		}
	}

	public void textEquals(String content) {
		assertEquals(content, string);
	}
	
	public void containsRequests(StringBuffer page, String... requests) {
		// TODO Auto-generated method stub
		
	}
}
