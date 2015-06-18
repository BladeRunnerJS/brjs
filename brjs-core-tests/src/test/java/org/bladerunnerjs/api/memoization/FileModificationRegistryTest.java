package org.bladerunnerjs.api.memoization;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
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
		testRootDir = BRJSTestModelFactory.createRootTestDir();
		secondRootDir = BRJSTestModelFactory.createRootTestDir();
		dirInRoot = new File(testRootDir, "some-dir");
		fileInChildDir = new File(dirInRoot, "nested-file.txt");
		fileModificationRegistry = new FileModificationRegistry(new MatchFileFilter(testRootDir), FalseFileFilter.INSTANCE);
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
		fileModificationRegistry = new FileModificationRegistry(new MatchFileFilter(testRootDir, secondRootDir), FalseFileFilter.INSTANCE);
		fileModificationRegistry.incrementFileVersion( new File(secondRootDir, "foo/bar") );
		assertEquals(1, fileModificationRegistry.getFileVersion(secondRootDir));
	}
	
	
	
	private class MatchFileFilter extends AbstractFileFilter implements IOFileFilter {
		List<File> matchFiles;
		public MatchFileFilter(File... matchFiles) { this.matchFiles = Arrays.asList(matchFiles); }
		public boolean accept(File file) { return matchFiles.contains(file); }
		
	}
}
