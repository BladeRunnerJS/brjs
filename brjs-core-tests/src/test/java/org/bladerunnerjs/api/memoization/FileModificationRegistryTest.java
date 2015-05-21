package org.bladerunnerjs.api.memoization;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileModificationRegistryTest
{

	private File testRootDir;
	private File secondRootDir;
	
	private File dirInRoot;
	private File fileInChildDir;
	private FileModificationRegistry fileModificationRegistry;
	
	@Before
	public void setup() throws Exception {
		testRootDir = BRJSTestModelFactory.createTestSdkDirectory();
		secondRootDir = BRJSTestModelFactory.createTestSdkDirectory();
		dirInRoot = new File(testRootDir, "some-dir");
		fileInChildDir = new File(dirInRoot, "nested-file.txt");
		fileModificationRegistry = new FileModificationRegistry(FalseFileFilter.INSTANCE, testRootDir);
	}
	
	@After
	public void tearDown()
	{
		for (File dirToDelete : Arrays.asList(testRootDir, secondRootDir)) {
			if (dirToDelete != null) {
				FileUtils.deleteQuietly(dirToDelete);
			}			
		}
	}
	
	@Test
	public void versionStartsAtZero() throws Exception
	{
		assertEquals(0, fileModificationRegistry.getFileVersion(dirInRoot));
	}

	@Test
	public void versionIsIncrementedProperly() throws Exception
	{
		fileModificationRegistry.incrementFileVersion(dirInRoot);
		assertEquals(1, fileModificationRegistry.getFileVersion(dirInRoot));
	}
	
	@Test
	public void parentDirectoryVersionsAreUpdated() throws Exception
	{
		fileModificationRegistry.incrementFileVersion(fileInChildDir);
		assertEquals(1, fileModificationRegistry.getFileVersion(fileInChildDir));
		assertEquals(1, fileModificationRegistry.getFileVersion(dirInRoot));
	}
	
	@Test
	public void parentDirectoryVersionsAreUpdatedIncludingTheRootDir() throws Exception
	{
		fileModificationRegistry.incrementFileVersion(fileInChildDir);
		assertEquals(1, fileModificationRegistry.getFileVersion(testRootDir));
	}
	
	@Test
	public void parentDirectoryVersionsAreUpdatedIncludingTheSecondRootDir() throws Exception
	{
		fileModificationRegistry = new FileModificationRegistry(FalseFileFilter.INSTANCE, testRootDir, secondRootDir);
		fileModificationRegistry.incrementFileVersion( new File(secondRootDir, "foo/bar") );
		assertEquals(1, fileModificationRegistry.getFileVersion(secondRootDir));
	}
	
}
