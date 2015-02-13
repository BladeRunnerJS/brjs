package org.bladerunnerjs.memoization;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MemoizedFileTest
{

	private File tempDir;

	@Before
	public void setup() throws IOException {
		tempDir = FileUtils.createTemporaryDirectory(this.getClass());
	}
	
	@After
	public void cleanup() {
		org.apache.commons.io.FileUtils.deleteQuietly(tempDir);
	}
	
	@Test
	public void testRelativePaths() throws InvalidSdkDirectoryException, IOException {
		BRJS brjs = BRJSTestModelFactory.createModel( tempDir );
		assertEquals("child", brjs.getMemoizedFile(new File(".")).getRelativePath(brjs.getMemoizedFile(new File("child"))));
		assertEquals("child/grandchild", brjs.getMemoizedFile(new File(".")).getRelativePath(brjs.getMemoizedFile(new File("child/grandchild"))));
		assertEquals("../child/grandchild", brjs.getMemoizedFile(new File(".")).getRelativePath(brjs.getMemoizedFile(new File("../child/grandchild"))));
	}
	
}
