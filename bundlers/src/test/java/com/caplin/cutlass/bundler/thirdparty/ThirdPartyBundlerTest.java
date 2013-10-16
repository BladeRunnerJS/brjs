package com.caplin.cutlass.bundler.thirdparty;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.bladerunnerjs.model.sinbin.AppMetaData;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.APPLICATIONS_DIR;
import static org.bladerunnerjs.model.sinbin.CutlassConfig.SDK_DIR;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThirdPartyBundlerTest
{
	private static final File BASE_DIR1 = new File("src/test/resources/thirdparty-bundler").getAbsoluteFile();
	private static final File APP_BASE1 = new File(BASE_DIR1, APPLICATIONS_DIR + "/app1");
	
	@Test
	public void testLibraryResourceForLibraryPresentBothInAppAndSdkIsBundledFromApp() throws Exception
	{
		ThirdPartyBundler bundler = new ThirdPartyBundler();
		List<File> files = bundler.getBundleFiles(APP_BASE1, null, "thirdparty-libraries/lib1/lib1-resource.txt_thirdparty.bundle");
		assertEquals(Arrays.asList(new File(APP_BASE1, "thirdparty-libraries/lib1/lib1-resource.txt")), files);
	}
	
	@Test
	public void testLibraryResourceExistingInSdkButNotInAppForLibraryPresentBothInAppAndSdkIsNotBundled() throws Exception
	{
		ThirdPartyBundler bundler = new ThirdPartyBundler();
		String resourceName = "lib1-resource-not-existing-in-app.txt";
		File lib1Resource = new File(BASE_DIR1, SDK_DIR + "/libs/javascript/thirdparty/lib1/" + resourceName);
		List<File> files = bundler.getBundleFiles(APP_BASE1,
									null,
									"thirdparty-libraries/lib1/" + resourceName + "_thirdparty.bundle");
		
		assertTrue(lib1Resource.exists());
		assertEquals(0, files.size());
	}
	
	@Test
	public void testLibraryResourceForLibraryPresentOnlyInSdkIsBundledFromSdk() throws Exception
	{
		ThirdPartyBundler bundler = new ThirdPartyBundler();
		List<File> files = bundler.getBundleFiles(APP_BASE1, null, "thirdparty-libraries/lib2/lib2-resource.txt_thirdparty.bundle");
		assertEquals(Arrays.asList(new File(BASE_DIR1, SDK_DIR + "/libs/javascript/thirdparty/lib2/lib2-resource.txt")), files);
	}
	
	@Test
	public void testParserDoesntBlowUpOnQueryString() throws Exception
	{
		ThirdPartyBundler bundler = new ThirdPartyBundler();
		List<File> files = bundler.getBundleFiles(APP_BASE1, null, "thirdparty-libraries/lib1/lib1-resource.txt_thirdparty.bundle?somequery=1234");
		assertEquals(Arrays.asList(new File(APP_BASE1, "thirdparty-libraries/lib1/lib1-resource.txt")), files);
	}
	
	@Test
	public void testCanGetValidRequestStrings() throws Exception
	{
		AppMetaData appMetaData = mock(AppMetaData.class);
		when(appMetaData.getApplicationDirectory()).thenReturn(APP_BASE1);

		ThirdPartyBundler bundler = new ThirdPartyBundler();
		List<String> requestStrings = bundler.getValidRequestStrings(appMetaData);
		
		assertEquals(5, requestStrings.size());
		
		assertTrue(requestStrings.contains("thirdparty-libraries/lib1/lib1-resource.txt_thirdparty.bundle"));
		assertTrue(requestStrings.contains("thirdparty-libraries/lib2/lib2-resource.txt_thirdparty.bundle"));
		assertTrue(requestStrings.contains("thirdparty-libraries/lib3/lib3-resource1-in-sdk.txt_thirdparty.bundle"));
		assertTrue(requestStrings.contains("thirdparty-libraries/lib3/lib3-resource2-in-sdk.txt_thirdparty.bundle"));
		assertTrue(requestStrings.contains("thirdparty-libraries/lib4/lib4-resource1-with_underscore-in-sdk.txt_thirdparty.bundle"));
	}
	
	@Test
	public void ifWePassInARootDirectoryWeJustDontGetAppSpecificResults() throws Exception
	{
		ThirdPartyBundler bundler = new ThirdPartyBundler();
		
		List<File> files = bundler.getBundleFiles(BASE_DIR1, null, "thirdparty-libraries/lib1/lib1-resource.txt_thirdparty.bundle");
		assertEquals(Arrays.asList(new File(BASE_DIR1, "sdk/libs/javascript/thirdparty/lib1/lib1-resource.txt")), files);
	}
	
	@Test
	public void ifWePassInAnInvalidDirectoryWeGetNoResultsWhatsoever() throws Exception
	{
		ThirdPartyBundler bundler = new ThirdPartyBundler();
		List<File> files = bundler.getBundleFiles(new File(""), null, "thirdparty-libraries/lib1/lib1-resource.txt_thirdparty.bundle");
		
		assertEquals(0, files.size());
	}
}
