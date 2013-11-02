package org.bladerunnerjs.specutil.engine;

import static org.junit.Assert.*;

public class StringVerifier {
	private String string;
	
	public StringVerifier(SpecTest specTest, String string) {
		this.string = string;
	}
	
	public void containsText(String substring) {
		assertTrue(string.contains(substring));
	}
}
