package com.caplin.cutlass.app.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import com.caplin.cutlass.util.FileUtility;


public class RestApiServiceTest
{

	private static final String NO_APPS_PATH = "src/test/resources/RestApiServiceTest/no-apps"; 
	private static final String ONE_APP_PATH = "src/test/resources/RestApiServiceTest/one-app"; 
	private static final String THREE_APPS_PATH = "src/test/resources/RestApiServiceTest/three-apps"; 
	private static final String MORE_APPS_PATH = "src/test/resources/RestApiServiceTest/more-apps"; 
	
	private RestApiService service;
	
	/* get apps tests */
	
	@Test
	public void testGettingApps_NoApps()
	{
		setupService(new File(NO_APPS_PATH));
		assertEquals( "[]", service.getApps() );
	}
	
	@Test
	public void testGettingApps_SingleApp()
	{
		setupService(new File(ONE_APP_PATH));
		assertEquals( "[\"app1\"]", service.getApps() );
	}
	
	@Test
	public void testGettingApps_MultipleApps()
	{
		setupService(new File(THREE_APPS_PATH));
		assertEquals( "[\"app1\", \"app2\", \"app3\"]", service.getApps() );
	}
	
	
	/* get app tests */
	
	@Test
	public void testGetApp_SingleEmptyBladeset() throws Exception
	{
		setupService(new File(MORE_APPS_PATH));
		assertEquals( "{\"a\":[]}", service.getApp("single-bladeset-app") );
	}
	
	@Test
	public void testGetApp_SingleBladesetWithOneBlade() throws Exception
	{
		setupService(new File(MORE_APPS_PATH));
		assertEquals( "{\"a\":[\"a-blade\"]}", service.getApp("single-bladeset-single-blade-app") );
	}
	
	@Test
	public void testGetApp_SingleBladesetWithMultipleBlades() throws Exception
	{
		setupService(new File(MORE_APPS_PATH));
		assertEquals( "{\"a\":[\"a-blade\", \"another-blade\"]}", service.getApp("single-bladeset-multi-blade-app") );
	}
	
	@Test
	public void testGetApp_MultiBladesetWithMultipleBlades() throws Exception
	{
		setupService(new File(MORE_APPS_PATH));
		assertEquals( "{\"a\":[\"a-blade\", \"another-blade\"], \"another\":[\"a-blade\", \"another-blade\"]}", service.getApp("multi-bladeset-multi-blade-app") );
	}
	
	@Test(expected=Exception.class)
	public void testGetApp_unknownAppThrowsException() throws Exception
	{
		setupService(new File(MORE_APPS_PATH));
		service.getApp("app-doesnt-exist");
	}
	
	
	/* create app tests */
	
	@Test
	public void testCreatingANewApp() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(ONE_APP_PATH));
		setupService(temporarySdk);
		service.createApp("aNewApp", "appx");
		assertTrue( new File(temporarySdk.getParentFile(), "apps/aNewApp").exists() );
	}

	@Test(expected=Exception.class)
	public void testCreatingANewAppThatAlreadyExists() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(THREE_APPS_PATH));
		setupService(temporarySdk);
		service.createApp("app2", "appx");
	}
	
	
	/* get app thumbnail tests */
	
	@Test
	public void testGettingThumnailLocationForApp_thumbExists() throws Exception
	{
		setupService(new File(THREE_APPS_PATH));
		assertEquals( new File("src/test/resources/RestApiServiceTest/three-apps/apps/app1/thumb.png").getAbsoluteFile(), service.getAppImageLocation("app1") );
	}
	
	@Test
	public void testGettingThumnailLocationForApp_thumbDoesntExist() throws Exception
	{
		setupService(new File(THREE_APPS_PATH));
		assertEquals( null, service.getAppImageLocation("app2") );
	}
	
	
	/* import motif tests */
	
	@Test
	public void testImportingAMotif() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(ONE_APP_PATH));
		setupService(temporarySdk);
		service.importMotif("aNewApp", "appx", new File("src/test/resources/RestApiServiceTest/single-bladeset-single-blade-app.zip"));
		assertTrue( new File(temporarySdk.getParentFile(), "apps/aNewApp").exists() );
	}
	
	@Test(expected=Exception.class)
	public void testImportingAMotifWhenAppNameAlreadyExists() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(ONE_APP_PATH));
		setupService(temporarySdk);
		service.importMotif("app1", "appx", new File("src/test/resources/RestApiServiceTest/single-bladeset-single-blade-app.zip"));
	}
	
	
	/* export war tests */
	
	@Test
	public void testExportingAWar() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(ONE_APP_PATH));
		setupService(temporarySdk);
		File warFile = FileUtility.createTemporaryFile("app1-war", ".war");
		warFile.delete();
		assertFalse( warFile.exists() );
		service.exportWar("app1", warFile);
		assertTrue( warFile.exists() );
	}
	
	@Test(expected=Exception.class)
	public void testExportingAWarForNonExistantApp() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(ONE_APP_PATH));
		setupService(temporarySdk);
		File warFile = FileUtility.createTemporaryFile("app1-war", ".war");
		warFile.delete();
		assertFalse( warFile.exists() );
		service.exportWar("i-dont-exist", warFile);
	}
	
	
	/* test import existing bladeset */
	
	@Test
	public void testImportingExistingBladeset() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(MORE_APPS_PATH));
		setupService(temporarySdk);
		
		service.createApp("my-new-app", "appx");
		assertTrue( new File(temporarySdk.getParentFile(), "apps/my-new-app").exists() );
		
		Map<String,Map<String,List<String>>> bladesets = new HashMap<String,Map<String,List<String>>>();
		Map<String,List<String>> newBladeset = new HashMap<String,List<String>>();
		newBladeset.put("newBladesetName", Arrays.asList("a"));
		newBladeset.put("blades", Arrays.asList("a-blade","another-blade"));
		bladesets.put("a", newBladeset);
		
		service.importBladeset("multi-bladeset-multi-blade-app", bladesets, "my-new-app");
		assertTrue( new File(temporarySdk.getParentFile(), "apps/my-new-app/a-bladeset/").exists() );
		assertTrue( new File(temporarySdk.getParentFile(), "apps/my-new-app/a-bladeset/blades/a-blade").exists() );
		assertTrue( new File(temporarySdk.getParentFile(), "apps/my-new-app/a-bladeset/blades/another-blade").exists() );
	}
	
	@Test
	public void testImportingExistingBladesetExcludingSomeBlades() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(MORE_APPS_PATH));
		setupService(temporarySdk);
		
		service.createApp("my-new-app", "appx");
		assertTrue( new File(temporarySdk.getParentFile(), "apps/my-new-app").exists() );
		
		Map<String,Map<String,List<String>>> bladesets = new HashMap<String,Map<String,List<String>>>();
		Map<String,List<String>> newBladeset = new HashMap<String,List<String>>();
		newBladeset.put("newBladesetName", Arrays.asList("a"));
		newBladeset.put("blades", Arrays.asList("a-blade"));
		bladesets.put("a", newBladeset);
		
		service.importBladeset("multi-bladeset-multi-blade-app", bladesets, "my-new-app");
		assertTrue( new File(temporarySdk.getParentFile(), "apps/my-new-app/a-bladeset/").exists() );
		assertTrue( new File(temporarySdk.getParentFile(), "apps/my-new-app/a-bladeset/blades/a-blade").exists() );
		assertFalse( new File(temporarySdk.getParentFile(), "apps/my-new-app/a-bladeset/blades/another-blade").exists() );
	}
	
	@Test
	public void testImportingExistingBladesetWithANewName() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(MORE_APPS_PATH));
		setupService(temporarySdk);
		
		service.createApp("my-new-app", "appx");
		assertTrue( new File(temporarySdk.getParentFile(), "apps/my-new-app").exists() );
		
		Map<String,Map<String,List<String>>> bladesets = new HashMap<String,Map<String,List<String>>>();
		Map<String,List<String>> newBladeset = new HashMap<String,List<String>>();
		newBladeset.put("newBladesetName", Arrays.asList("newa"));
		newBladeset.put("blades", Arrays.asList("a-blade","another-blade"));
		bladesets.put("a", newBladeset);
		
		service.importBladeset("multi-bladeset-multi-blade-app", bladesets, "my-new-app");
		assertFalse( new File(temporarySdk.getParentFile(), "apps/my-new-app/a-bladeset/").exists() );
		assertTrue( new File(temporarySdk.getParentFile(), "apps/my-new-app/newa-bladeset/").exists() );
		assertTrue( new File(temporarySdk.getParentFile(), "apps/my-new-app/newa-bladeset/blades/a-blade").exists() );
		assertTrue( new File(temporarySdk.getParentFile(), "apps/my-new-app/newa-bladeset/blades/another-blade").exists() );
	}
	
	/* create bladeset tests */
	
	@Test
	public void testCreatingNewBladeset() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(MORE_APPS_PATH));
		setupService(temporarySdk);
		service.createBladeset("multi-bladeset-multi-blade-app", "basic");
		assertTrue( new File(temporarySdk.getParentFile(), "apps/multi-bladeset-multi-blade-app/basic-bladeset").exists() );
	}
	
	@Test(expected=Exception.class)
	public void testCreatingNewBladesetWhenBladesetExists() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(MORE_APPS_PATH));
		setupService(temporarySdk);
		service.createBladeset("multi-bladeset-multi-blade-app", "a");
	}
	
	
	/* create blade tests */
	
	@Test
	public void testCreatingNewBlade() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(MORE_APPS_PATH));
		setupService(temporarySdk);
		service.createBlade("multi-bladeset-multi-blade-app", "a", "basic");
		assertTrue( new File(temporarySdk.getParentFile(), "apps/multi-bladeset-multi-blade-app/a-bladeset/blades/basic").exists() );
	}
	
	@Test(expected=Exception.class)
	public void testCreatingNewBladeWhenBladeExists() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(MORE_APPS_PATH));
		setupService(temporarySdk);
		assertTrue( new File(temporarySdk.getParentFile(), "apps/multi-bladeset-multi-blade-app/a-bladeset/blades/a-blade").exists() );
		service.createBlade("multi-bladeset-multi-blade-app", "a", "a-blade");
	}
	
	
	/* release note tests */
	@Test
	public void testGettingLatestReleaseNote() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(MORE_APPS_PATH));
		setupService(temporarySdk);
		String cmdOutput = service.getCurrentReleaseNotes();
		assertEquals( "latest release note", cmdOutput );
	}
	
	/* run tests tests */
	@Test
	public void testRunBladesetTests() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(MORE_APPS_PATH));
		setupService(temporarySdk);
		
		try {
			service.runBladesetTests("multi-bladeset-multi-blade-app", "a", "ALL");
		} 
		catch (Exception ex)
		{
			//TODO: we dont want to actually run the tests here - find a better way to check the test command is executed
			assertTrue( ex.getMessage().contains("Test runner config file does not exist"));
		}
	}
	
	@Test
	public void testRunBladeTests() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(MORE_APPS_PATH));
		setupService(temporarySdk);
		
		try 
		{
			service.runBladeTests("multi-bladeset-multi-blade-app", "a", "a-blade", "ALL");
		} 
		catch (Exception ex)
		{
			//TODO: we dont want to actually run the tests here - find a better way to check the test command is executed
			assertTrue( ex.getMessage().contains("Test runner config file does not exist"));
		}
	}
	
	
	// TODO: re-enable this test once we are using a stream handlers to keep the process from blocking
	@Ignore
	@Test
	public void testgetJsdocForApp() throws Exception
	{
		File temporarySdk = FileUtility.createTemporarySdkInstall(new File(MORE_APPS_PATH));
		setupService(temporarySdk);
		App app1 = BRJSAccessor.root.app("app1");
		File indexFile = new File(app1.storageDir("jsdoc-toolkit"), "output/index.html");
		
		assertFalse(indexFile.exists());
		
		app1.create();
		service.getJsdocForApp("app1");
		
		assertTrue(indexFile.exists());
	}
	
	private void setupService(File sdkRoot)
	{
		BRJS brjs = BRJSTestFactory.createBRJS(sdkRoot);
		BRJSAccessor.initialize(brjs);
		service = new RestApiService(brjs);
	}
	
}
