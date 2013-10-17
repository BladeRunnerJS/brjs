package com.caplin.cutlass.command.check;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class CheckUtilityTest {

	private CheckUtility checkUtility = new CheckUtility();
	private static final File parentTestFolder = new File("src/test/resources/CheckUtilityTest");
	

	@Test
	public void testFileContentsAreEqualCanFigureOutJarsAreTheSame() throws IOException {
		File jarA = new File(parentTestFolder, "jars-are-same/jarA/br-same.jar");
		File copyOfJarA = new File(parentTestFolder, "jars-are-same/jarA-copy-to-compare-with/br-same.jar");
		assertTrue(FileUtils.contentEquals(jarA, copyOfJarA));		
	}
	
	@Test
	public void testFileContentsAreEqualCanTellJarsAreDifferent() throws IOException
	{
		File fileA = new File(parentTestFolder, "jars-are-same/jarA/br-same.jar");
		File fileB = new File(parentTestFolder, "jars-are-same/jarB/br-different.jar");
		assertFalse(FileUtils.contentEquals(fileA, fileB));		
	}
	
	@Test (expected=Exception.class)
	public void testApplicationWithoutWebInfLib() throws Exception
	{
		File applicationDir = new File(parentTestFolder, "application-no-web-inf-lib");
		
		assertTrue(applicationDir.exists());
		assertFalse(new File (applicationDir, "WEB-INF/lib").exists());
		
		checkUtility.getJarsFromApplication(applicationDir);	
	}

	@Test
	public void testGetJarsFromApplicationOnlyReturnsFilesWithCaplinPrefix() throws Exception
	{
		File applicationDir = new File(parentTestFolder, "application-with-jars/firstapplication");
		File libDir = new File(applicationDir, "WEB-INF/lib");
		
		assertTrue(libDir.listFiles().length == 3);
		assertTrue(new File(libDir, "non-caplin.jar").exists());
		
		List<File> list = checkUtility.getJarsFromApplication(applicationDir);
		
		assertEquals(list.size(), 2);
		assertTrue(list.get(0).getName().equals("br-jar1.jar"));
		assertTrue(list.get(1).getName().equals("br-jar2.jar"));
	}
	
	@Test // Jar A = 27 bytes, Jar B = 0 bytes
	public void testJarsHaveSameNameButSizeIsDifferent() throws IOException
	{
		File jarA = new File(parentTestFolder, "jars-have-same-name-but-different-size/jarA/br-same-name.jar");
		File jarB = new File(parentTestFolder, "jars-have-same-name-but-different-size/jarB/br-same-name.jar");

		assertFalse(FileUtils.contentEquals(jarA, jarB));
	}
}
