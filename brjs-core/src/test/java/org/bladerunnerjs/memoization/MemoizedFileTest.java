package org.bladerunnerjs.memoization;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.TestModelAccessor;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.Test;


public class MemoizedFileTest extends TestModelAccessor
{

	@Test
	public void testRelativePaths() throws InvalidSdkDirectoryException, IOException {
		BRJS brjs = createModel( FileUtility.createTemporaryDirectory(this.getClass()) );
		assertEquals("child", brjs.getMemoizedFile(new File(".")).getRelativePath(brjs.getMemoizedFile(new File("child"))));
		assertEquals("child/grandchild", brjs.getMemoizedFile(new File(".")).getRelativePath(brjs.getMemoizedFile(new File("child/grandchild"))));
		assertEquals("../child/grandchild", brjs.getMemoizedFile(new File(".")).getRelativePath(brjs.getMemoizedFile(new File("../child/grandchild"))));
	}
	
}
