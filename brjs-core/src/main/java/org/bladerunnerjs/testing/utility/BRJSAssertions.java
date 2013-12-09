package org.bladerunnerjs.testing.utility;

import static org.junit.Assert.*;

public class BRJSAssertions {
	public static void assertContains(String expectedSubstring, String actualString) {
		if(!actualString.contains(expectedSubstring)) {
			assertEquals("'" + actualString + "' was expected to contain '" + expectedSubstring + "'.", expectedSubstring, actualString);
		}
	}
}
