package com.caplin.cutlass.command.importing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.sinbin.CutlassConfig;
import org.bladerunnerjs.model.utility.FileUtility;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.SDK_DIR;

public class ImportApplicationCommandUtilityTest
{
	
	private final ImportApplicationCommandUtility importApplicationCommandUtility = new ImportApplicationCommandUtility();
	
	private final File testResourceFolder = new File("src/test/resources/ImportApplicationCommandUtility");
	private File tempDir, sdkOnlySdkDir, sdkWithAppSdkDir; 

	private File temporaryDirectoryForNewApplication;

	
	@Before
	public void setUp() throws IOException
	{
		tempDir = FileUtility.createTemporaryDirectory("tempDirForResources");
		temporaryDirectoryForNewApplication = FileUtility.createTemporaryDirectory("tempApplicationDir");

		FileUtility.copyDirectoryContents(testResourceFolder, tempDir);
		sdkOnlySdkDir = new File(tempDir, "sdk-only/" + SDK_DIR);
		sdkWithAppSdkDir = new File(tempDir, "sdk-with-app/" + SDK_DIR);
	}
	
	@After
	public void tearDown() throws Exception
	{
		FileUtility.deleteDirAndContents(tempDir);
	}
	
	@Test (expected=CommandOperationException.class)
	public void verifyThatApplicationByUserRequestedNameThrowsErrorIfApplicationNameIsAlreadyUsed() throws Exception
	{
		assertTrue(new File(sdkWithAppSdkDir.getParentFile(), CutlassConfig.APPLICATIONS_DIR + "/emptytrader").exists() == true);
		
		importApplicationCommandUtility.createApplicationDirIfItDoesNotAlreadyExist(sdkWithAppSdkDir, "emptytrader");
	}
	
	@Test
	public void verifyThatApplicationByUserRequestedNameReturnsCorrectApplicationDirectory() throws Exception
	{
		File applicationsFolder = new File(sdkOnlySdkDir.getParentFile(), CutlassConfig.APPLICATIONS_DIR);
		assertFalse(applicationsFolder.exists());
		
		importApplicationCommandUtility.createApplicationDirIfItDoesNotAlreadyExist(sdkOnlySdkDir, "emptytrader");
		
		File newAppDir = new File(applicationsFolder, "emptytrader");
		
		assertEquals(true, newAppDir.exists());
		assertEquals("emptytrader", newAppDir.getName());
		assertEquals(applicationsFolder, newAppDir.getParentFile());
	}
	
	@Test (expected=CommandOperationException.class)
	public void unzipApplicationToTemporaryDirectoryForNewApplicationFailsIfZipFileCannotBeFound() throws Exception
	{
		importApplicationCommandUtility.unzipApplicationToTemporaryDirectoryForNewApplication("nonExisting.zip", sdkOnlySdkDir, FileUtility.createTemporaryDirectory("tempApplicationDir"));
	}
	
	@Test (expected=CommandOperationException.class)
	public void unzipApplicationToTemporaryDirectoryByPassingInAnApplicationZipParameterWhichIsNotAZipFile() throws Exception
	{
		importApplicationCommandUtility.unzipApplicationToTemporaryDirectoryForNewApplication("emptytrader", sdkOnlySdkDir, temporaryDirectoryForNewApplication);
	}
	
	@Test
	public void unzipApplicationToTemporaryDirectoryForNewApplication() throws Exception
	{
		File newAppDir = new File(temporaryDirectoryForNewApplication, "emptytrader");
		assertFalse(newAppDir.exists());
		
		importApplicationCommandUtility.unzipApplicationToTemporaryDirectoryForNewApplication("emptytrader.zip", sdkOnlySdkDir, temporaryDirectoryForNewApplication);
		
		assertTrue(newAppDir.exists());
	}
	
	@Test
	public void copyOverCutlassSDKJavaLibsIntoTemporaryDirectoryForNewApplicationWEBINF() throws Exception
	{
		File newAppDir = new File(temporaryDirectoryForNewApplication, "emptytrader");

		importApplicationCommandUtility.unzipApplicationToTemporaryDirectoryForNewApplication("emptytrader.zip", sdkOnlySdkDir, temporaryDirectoryForNewApplication);
		
		File webINFFolderInNewApplicationTemporaryDirectory = new File(newAppDir, "WEB-INF/lib");
		File jarFileInNewApplicationTemporaryDirectory = new File(webINFFolderInNewApplicationTemporaryDirectory, "testJar.jar");
		
		assertEquals(4, webINFFolderInNewApplicationTemporaryDirectory.list().length);
		assertTrue(new File(webINFFolderInNewApplicationTemporaryDirectory, "keymaster.jar").exists());
		assertTrue(new File(webINFFolderInNewApplicationTemporaryDirectory, "bcprov-jdk14-1.46.jar").exists());
		assertTrue(new File(webINFFolderInNewApplicationTemporaryDirectory, "log4j-1.2.17.jar").exists());
		assertTrue(new File(webINFFolderInNewApplicationTemporaryDirectory, "slf4j-log4j12-1.6.6.jar").exists());
		assertFalse(jarFileInNewApplicationTemporaryDirectory.exists());
		
		importApplicationCommandUtility.copyCutlassSDKJavaLibsIntoApplicationWebInfDirectory(sdkOnlySdkDir, new File(temporaryDirectoryForNewApplication,  "emptytrader"));
		
		assertEquals(5, webINFFolderInNewApplicationTemporaryDirectory.list().length);
		assertTrue(jarFileInNewApplicationTemporaryDirectory.exists());
	}
	
	@Test
	public void getCurrentApplicationName() throws Exception
	{
		importApplicationCommandUtility.unzipApplicationToTemporaryDirectoryForNewApplication("emptytrader.zip", sdkOnlySdkDir, temporaryDirectoryForNewApplication);
		
		String applicationName = importApplicationCommandUtility.getCurrentApplicationName(temporaryDirectoryForNewApplication);
		
		assertEquals("emptytrader", applicationName);
	}
}
