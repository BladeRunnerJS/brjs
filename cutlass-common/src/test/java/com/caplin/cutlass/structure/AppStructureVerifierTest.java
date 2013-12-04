package com.caplin.cutlass.structure;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.util.FileUtility;
import org.bladerunnerjs.model.sinbin.CutlassConfig;

public class AppStructureVerifierTest 
{

	private final File testResourceFolder = new File("src/test/resources/AppStructureVerifierTest");
	private File tempDir, sdkDirWithApp;

	@Before
	public void setUp() throws IOException
	{
		tempDir = FileUtility.createTemporaryDirectory("tempDirForResources");
		sdkDirWithApp = new File(tempDir, "sdk-with-app/" + CutlassConfig.SDK_DIR);
		FileUtility.copyDirectoryContents(testResourceFolder, tempDir);
	}
	
	@After
	public void tearDown() throws IOException
	{
		tempDir.delete();
	}
	
	@Test
	public void testApplicationExists()
	{
		assertTrue(AppStructureVerifier.applicationExists(sdkDirWithApp, "emptytrader"));
	}
	
	@Test 
	public void testApplicationExistsWhenItDoesNot()
	{
		assertFalse(AppStructureVerifier.applicationExists(sdkDirWithApp, "doesnotexist"));
	}
	
	public void testGetApplicationDirFileObject()
	{
		File emptytraderDir = new File(sdkDirWithApp.getParentFile(), CutlassConfig.APPLICATIONS_DIR + File.separator + "emptytrader");
		File retreivedAppDir = AppStructureVerifier.getApplicationDirFileObject(sdkDirWithApp, "emptytrader"); 
				
		assertTrue(emptytraderDir.exists());
		assertEquals(emptytraderDir.getAbsolutePath(), retreivedAppDir.getAbsolutePath());
	}
	
	@Test
	public void testGetApplicationDirFileObjectWhenAppDoesNotExist()
	{
		File nonExistingApp = new File(sdkDirWithApp.getParentFile(), CutlassConfig.APPLICATIONS_DIR + File.separator + "doesnotexist");
		File retreivedAppDir = AppStructureVerifier.getApplicationDirFileObject(sdkDirWithApp, "doesnotexist"); 
				
		assertFalse(nonExistingApp.exists());
		assertEquals(nonExistingApp.getAbsolutePath(), retreivedAppDir.getAbsolutePath());
	}
	
	@Test
	public void testBladesetExists()
	{
		assertTrue(AppStructureVerifier.bladesetExists(sdkDirWithApp, "emptytrader", "fx"));
	}
	
	@Test
	public void testBladesetExistsWhenItDoesNot()
	{
		assertFalse(AppStructureVerifier.bladesetExists(sdkDirWithApp, "emptytrader", "fi"));
	}
	
	@Test
	public void testGetBladesetFileObject()
	{
		File fxBladesetDir = new File(sdkDirWithApp.getParentFile(), CutlassConfig.APPLICATIONS_DIR + File.separator + "emptytrader/fx-bladeset");
		File retreivedBladesetDir = AppStructureVerifier.getBladesetDirFileObject(sdkDirWithApp, "emptytrader", "fx");
		
		assertTrue(fxBladesetDir.exists());
		assertEquals(fxBladesetDir.getAbsolutePath(), retreivedBladesetDir.getAbsolutePath());
	}
	
	@Test
	public void testGetBladesetFileObjectWhenBladesetDoesNotExist()
	{
		File nonExistingBladesetDir = new File(sdkDirWithApp.getParentFile(), CutlassConfig.APPLICATIONS_DIR + File.separator + "emptytrader/ghost-bladeset");
		File retreivedBladesetDir = AppStructureVerifier.getBladesetDirFileObject(sdkDirWithApp, "emptytrader", "ghost");
		
		assertFalse(nonExistingBladesetDir.exists());
		assertEquals(nonExistingBladesetDir.getAbsolutePath(), retreivedBladesetDir.getAbsolutePath());
	}
	
	@Test
	public void testBladeExists()
	{
		assertTrue(AppStructureVerifier.bladeExists(sdkDirWithApp, "emptytrader", "fx", "grid"));
	}
	
	@Test
	public void testBladeExistsWhenItDoesNot()
	{
		assertFalse(AppStructureVerifier.bladeExists(sdkDirWithApp, "emptytrader", "fx", "doesnotexistblade"));
	}	
	
	@Test
	public void testGetBladeDirFileObject()
	{
		File appDir = new File(sdkDirWithApp.getParentFile(), CutlassConfig.APPLICATIONS_DIR + File.separator + "emptytrader");
		File bladeDir = new File(appDir, "fx-bladeset" +  File.separator + CutlassConfig.BLADES_CONTAINER_DIR + File.separator + "grid");
		File retreivedBladeDir = AppStructureVerifier.getBladeDirFileObject(sdkDirWithApp, "emptytrader", "fx-bladeset", "grid");
		
		assertTrue(bladeDir.exists());
		assertEquals(bladeDir.getAbsolutePath(), retreivedBladeDir.getAbsolutePath());
	}
	
	@Test 
	public void testChompFromBladeSet()
	{
		assertEquals(AppStructureVerifier.chompBladesetFromString("fx-bladeset"), "fx");
		assertEquals(AppStructureVerifier.chompBladesetFromString("fx"), "fx");
	}
	
	@Test 
	public void testQualifyBladesetFolderName()
	{
		assertEquals(AppStructureVerifier.qualifyBladesetFolderName("fx-bladeset"), "fx-bladeset");
		assertEquals(AppStructureVerifier.qualifyBladesetFolderName("fx"), "fx-bladeset");
	}
	
	@Test 
	public void testChompFromAspect()
	{
		assertEquals(AppStructureVerifier.chompAspectFromString("mobile-aspect"), "mobile");
		assertEquals(AppStructureVerifier.chompAspectFromString("mobile"), "mobile");
	}
	
	@Test 
	public void testQualifyAspectFolderName()
	{
		assertEquals(AppStructureVerifier.qualifyAspectFolderName("mobile-aspect"), "mobile-aspect");
		assertEquals(AppStructureVerifier.qualifyAspectFolderName("mobile"), "mobile-aspect");
	}

}
