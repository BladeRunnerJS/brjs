package org.bladerunnerjs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.template.DirectoryAlreadyExistsException;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.TemplateUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class TemplateUtilityTest
{
	private BRJS brjs;
	
	@Before
	public void setUp() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("TemplateUtilityTest");
		FileUtils.copyDirectory(new File("src/test/resources/TemplateUtilityTest"), tempDir);
		brjs = BRJSTestFactory.createBRJS(tempDir);
	}
	
	@After
	public void tearDown() {
		brjs.close();
	}
	
	@Test(expected=DirectoryAlreadyExistsException.class)
	public void installingATemplateToAPreExistingDirectoryCausesAnException() throws Exception
	{
		TemplateUtility.installTemplate(brjs.app("pre-existing-app"), "app", new HashMap<String, String>());
	}
	
	@Test
	public void templateCanBeInstalledWithoutTransformations() throws Exception
	{
		App app = brjs.app("app");
		assertFalse("app dir does not exist", app.dirExists());
		
		TemplateUtility.installTemplate(brjs.app("app"), "app", new HashMap<String, String>());
		
		assertTrue("app dir exists", app.dirExists());
		
		assertTrue("root dir exists", app.file("root-@dir").exists());
		assertTrue("root dir is a directory", app.file("root-@dir").isDirectory());
		assertTrue("root text file exists", app.file("root-@dir/root-text-file.txt").exists());
		assertEquals("root text file has correct contents", "this is the root text file (in root @dir)", FileUtils.readFileToString(app.file("root-@dir/root-text-file.txt")));
		assertTrue("root binary file exists", app.file("root-@dir/root-binary-file.bin").exists());
		assertEquals("root binary file has correct contents", "this is the root binary file (in root @dir)", FileUtils.readFileToString(app.file("root-@dir/root-binary-file.bin")));
		assertFalse("hidden file does not exist", app.file("root-@dir/.unix-hidden-file.txt").exists());
		
		assertTrue("child dir exists", app.file("root-@dir/child-@dir").exists());
		assertTrue("child dir is a directory", app.file("root-@dir/child-@dir").isDirectory());
		assertTrue("child text file exists", app.file("root-@dir/child-@dir/child-text-file.txt").exists());
		assertEquals("child text file has correct contents", "this is the child text file (in child @dir)", FileUtils.readFileToString(app.file("root-@dir/child-@dir/child-text-file.txt")));
		assertTrue("child binary file exists", app.file("root-@dir/child-@dir/child-binary-file.bin").exists());
		assertEquals("child binary file has correct contents", "this is the child binary file (in child @dir)", FileUtils.readFileToString(app.file("root-@dir/child-@dir/child-binary-file.bin")));
	}
	
	@Test
	public void fileNamesAndContentsCanBeModifiedWhenInstallingTemplate() throws Exception
	{
		App app = brjs.app("app");
		Map<String, String> transformations = new HashMap<>();
		
		transformations.put("dir", "folder");
		TemplateUtility.installTemplate(brjs.app("app"), "app", transformations);
		
		assertTrue("app folder exists", app.dirExists());
		
		assertTrue("root folder exists", app.file("root-folder").exists());
		assertTrue("root folder is a directory", app.file("root-folder").isDirectory());
		assertTrue("root text file exists", app.file("root-folder/root-text-file.txt").exists());
		assertEquals("root text file has correct contents", "this is the root text file (in root folder)", FileUtils.readFileToString(app.file("root-folder/root-text-file.txt")));
		assertTrue("root binary file exists", app.file("root-folder/root-binary-file.bin").exists());
		assertEquals("root binary file has correct contents", "this is the root binary file (in root @dir)", FileUtils.readFileToString(app.file("root-folder/root-binary-file.bin")));
		assertFalse("hidden file does not exist", app.file("root-dir/.unix-hidden-file.txt").exists());
		
		assertTrue("child folder exists", app.file("root-folder/child-folder").exists());
		assertTrue("child folder is a directory", app.file("root-folder/child-folder").isDirectory());
		assertTrue("child text file exists", app.file("root-folder/child-folder/child-text-file.txt").exists());
		assertEquals("child text file has correct contents", "this is the child text file (in child folder)", FileUtils.readFileToString(app.file("root-folder/child-folder/child-text-file.txt")));
		assertTrue("child binary file exists", app.file("root-folder/child-folder/child-binary-file.bin").exists());
		assertEquals("child binary file has correct contents", "this is the child binary file (in child @dir)", FileUtils.readFileToString(app.file("root-folder/child-folder/child-binary-file.bin")));
	}
}
