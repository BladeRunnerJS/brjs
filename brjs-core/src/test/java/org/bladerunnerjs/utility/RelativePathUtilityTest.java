package org.bladerunnerjs.utility;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.TestModelAccessor;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.junit.Test;

public class RelativePathUtilityTest extends TestModelAccessor {
	
	@Test
	public void testRelativePaths() throws InvalidSdkDirectoryException, IOException {
		BRJS brjs = createModel( FileUtility.createTemporaryDirectory("RelativePathUtilityTest") );
		assertEquals("child", RelativePathUtility.get(brjs, new File("."), new File("child")));
		assertEquals("child/grandchild", RelativePathUtility.get(brjs, new File("."), new File("child/grandchild")));
		assertEquals("../child/grandchild", RelativePathUtility.get(brjs, new File("."),new File("../child/grandchild")));
	}
	
}
