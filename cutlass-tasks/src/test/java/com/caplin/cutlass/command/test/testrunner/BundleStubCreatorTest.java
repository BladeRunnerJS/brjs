package com.caplin.cutlass.command.test.testrunner;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.util.FileUtility;

public class BundleStubCreatorTest
{
	private File blade;
	private File blotter;

	@Before
	public void setup() throws IOException
	{
		blade = createTempBlade(new File("src/test/resources/BundleStubCreatorTest/grid"));
	}
	
	@Test
	public void testJsBundleStubIsCreatedWhenNoParentFolderExists() throws Exception
	{
		assertParentFolderDoesNotExist(new File(blade, "tests/test-unit/js-test-driver/bundles/js"), "bundles");
		assertParentFolderDoesNotExist(new File(blade, "tests/test-unit/js-test-driver/bundles/js/js.bundle"), "js");
		
		assertFileDoesNotExist(new File(blade, "tests/test-unit/js-test-driver/bundles/js/js.bundle"));
		
		BundleStubCreator.createRequiredStubs(new File(blade, "tests/test-unit/js-test-driver/jsTestDriver.conf"));
		
		assertFileExists(new File(blade, "tests/test-unit/js-test-driver/bundles/js/js.bundle"));
	}
	
	@Test
	public void testJsBundleStubIsCreatedWhenParentFoldersExist() throws Exception
	{
		blotter = createTempBlade(new File("src/test/resources/BundleStubCreatorTest/blotter"));
		assertParentFolderExists(new File(blotter, "tests/test-unit/js-test-driver/bundles/js"), "bundles");
		assertParentFolderExists(new File(blotter, "tests/test-unit/js-test-driver/bundles/js/js.bundle"), "js");
		
		assertFileDoesNotExist(new File(blotter, "tests/test-unit/js-test-driver/bundles/js/js.bundle"));
		
		BundleStubCreator.createRequiredStubs(new File(blotter, "tests/test-unit/js-test-driver/jsTestDriver.conf"));
		
		assertFileExists(new File(blotter, "tests/test-unit/js-test-driver/bundles/js/js.bundle"));
	}

	@Test
	public void testJsBundleStubIsNotCreatedForWildcardDeclaration() throws Exception
	{
		assertFileDoesNotExist(new File(blade, "tests/test-unit/js-test-driver/bundles/js/*.bundle"));
		
		BundleStubCreator.createRequiredStubs(new File(blade, "tests/test-unit/js-test-driver/jsTestDriverWithWildcard.conf"));
		
		assertFileDoesNotExist(new File(blade, "tests/test-unit/js-test-driver/bundles/js/*.bundle"));
	}
	
	@Test
	public void testJsBundleStubIsNotCreatedIfItNotInABundlesDirectory() throws Exception
	{
		assertFileDoesNotExist(new File(blade, "tests/test-unit/js-test-driver/bundles/js/js.bundle"));
		
		BundleStubCreator.createRequiredStubs(new File(blade, "tests/test-unit/js-test-driver/jsTestDriverWithoutBundlesDir.conf"));
		
		assertFileDoesNotExist(new File(blade, "tests/test-unit/js-test-driver/bundles/js/js.bundle"));
	}
	
	private File createTempBlade(File exisingBlade) throws IOException
	{
		File tempDir = FileUtility.createTemporaryDirectory(this.getClass().getSimpleName());
		assertTrue(tempDir.exists() && tempDir.isDirectory());
		FileUtils.copyDirectory(exisingBlade.getParentFile(), tempDir);
		return new File(tempDir, exisingBlade.getName());
	}
	
	private static void assertFileExists(File file)
	{
		assertTrue("Expected " + file + " to exist", file.exists());
	}
	
	private static void assertFileDoesNotExist(File file)
	{
		assertFalse("Expected " + file + " to not exist", file.exists());
	}
	
	private static void assertParentFolderDoesNotExist(File file, String parentDirectory)
	{
		File parentFolder = file.getParentFile();
		assertFalse("Expected " + parentDirectory + " to not exist", parentFolder.exists());
		
	}
	
	private void assertParentFolderExists(File file, String parentDirectory) 
	{
		File parentFolder = file.getParentFile();
		assertTrue("Expected " + parentDirectory + " to exist", parentFolder.exists());
	}
}
