package com.caplin.cutlass.bundler.js;

import static org.junit.Assert.*;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.bundler.ThirdPartyLibraryFinder;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;

public class ThirdPartyFileFinderTest {

	private final String base = "src/test/resources/js-bundler/third-party-file-finder/";
	private final String appDir = base + APPLICATIONS_DIR + "/test-app1";
	
	@Test
	public void testGetThirdPartyLibraryFilesGetsCorrectListOfFilesForALibraryBasedOnLibraryManifest() throws Exception 
	{
		ThirdPartyFileFinder fileFinder = new ThirdPartyFileFinder(new ThirdPartyLibraryFinder());
		
		Set<String> thirdPartyLibraries = new LinkedHashSet<String>();
		thirdPartyLibraries.add("lib1");
		
		List<File> libraryFiles = fileFinder.getThirdPartyLibraryFiles(new File(appDir), thirdPartyLibraries);
		assertEquals(3, libraryFiles.size());
		assertTrue(libraryFiles.contains(new File(base, CutlassConfig.SDK_DIR + "/libs/javascript/thirdparty/jquery/jQuery.js").getAbsoluteFile()));
		assertTrue(libraryFiles.contains(new File(base, APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib1/lib1_1.js").getAbsoluteFile()));
		assertTrue(libraryFiles.contains(new File(base, APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib1/lib1_2.js").getAbsoluteFile()));
	}

	@Test(expected=Exception.class)
	public void testGetThirdPartyLibraryFilesThrowsExceptionWhenJsFileFromLibraryManifestDoesntExist() throws Exception 
	{
		ThirdPartyFileFinder fileFinder = new ThirdPartyFileFinder(new ThirdPartyLibraryFinder());
		
		Set<String> thirdPartyLibraries = new LinkedHashSet<String>();
		thirdPartyLibraries.add("lib2");
		
		fileFinder.getThirdPartyLibraryFiles(new File(appDir), thirdPartyLibraries);
	}
	
	@Test(expected=ContentProcessingException.class)
	public void testIfWePassInAnInvalidDirWeJustGetNoResults() throws Exception 
	{
		ThirdPartyFileFinder fileFinder = new ThirdPartyFileFinder(new ThirdPartyLibraryFinder());
		
		Set<String> thirdPartyLibraries = new LinkedHashSet<String>();
		thirdPartyLibraries.add("lib1");
		
		List<File> libraryFiles = fileFinder.getThirdPartyLibraryFiles(new File(""), thirdPartyLibraries);
		assertEquals(0, libraryFiles.size());
	}
}
