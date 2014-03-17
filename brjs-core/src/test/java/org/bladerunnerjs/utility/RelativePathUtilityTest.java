package org.bladerunnerjs.utility;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class RelativePathUtilityTest {
	@Test
	public void xxx() {
		assertEquals("child", RelativePathUtility.get(new File("."), new File("child")));
		assertEquals("child/grandchild", RelativePathUtility.get(new File("."), new File("child/grandchild")));
		assertEquals("../child/grandchild", RelativePathUtility.get(new File("."), new File("../child/grandchild")));
	}
}
