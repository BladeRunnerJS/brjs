package org.bladerunnerjs.memoization;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.Test;


public class MemoizedFileTest
{

	@Test
	public void testRelativePaths() throws InvalidSdkDirectoryException, IOException {
		BRJS brjs = BRJSTestModelFactory.createModel( FileUtils.createTemporaryDirectory(this.getClass()) );
		assertEquals("child", brjs.getMemoizedFile(new File(".")).getRelativePath(brjs.getMemoizedFile(new File("child"))));
		assertEquals("child/grandchild", brjs.getMemoizedFile(new File(".")).getRelativePath(brjs.getMemoizedFile(new File("child/grandchild"))));
		assertEquals("../child/grandchild", brjs.getMemoizedFile(new File(".")).getRelativePath(brjs.getMemoizedFile(new File("../child/grandchild"))));
	}
	
}
