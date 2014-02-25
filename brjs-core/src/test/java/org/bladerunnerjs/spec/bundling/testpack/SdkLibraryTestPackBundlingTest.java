package org.bladerunnerjs.spec.bundling.testpack;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class SdkLibraryTestPackBundlingTest extends SpecTest
{
	private JsLib sdkLib;
	private TestPack sdkLibUTs, sdkLibATs;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			
			sdkLib = brjs.sdkLib("brjsLib");
		
			sdkLibUTs = sdkLib.testType("unit").testTech("TEST_TECH");
			sdkLibATs = sdkLib.testType("acceptance").testTech("TEST_TECH");
	}
	
	// N A M E S P A C E D - J S
	@Test
	public void weBundleSdkLibFilesInUTs() throws Exception {
		given(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).hasClass("brjsLib.Class1")
			.and(sdkLibUTs).testRefersTo("pkg/test.js", "brjsLib.Class1");
		then(sdkLibUTs).bundledFilesEquals(sdkLib.assetLocation("src").file("brjsLib/Class1.js"));
	}
	
	@Test
	public void weBundleSdkLibFilesInATs() throws Exception {
		given(sdkLib).hasNamespacedJsPackageStyle()
		.and(sdkLib).hasClass("brjsLib.Class1")
		.and(sdkLibATs).testRefersTo("pkg/test.js", "brjsLib.Class1");
	then(sdkLibATs).bundledFilesEquals(sdkLib.assetLocation("src").file("brjsLib/Class1.js"));
	}
	
	@Test
	public void noExceptionsAreThrownIfTheSdkLibSrcFolderHasAHiddenFolder() throws Exception {
		given(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).hasClass("brjsLib.Class1")
			.and(sdkLib).containsFileWithContents("src/.svn/generatedSvnFile.txt", "generatedContent")
			.and(sdkLibUTs).testRefersTo("pkg/test.js", "brjsLib.Class1");
		then(sdkLibUTs).bundledFilesEquals(sdkLib.assetLocation("src").file("brjsLib/Class1.js"));
	}
	
	@Test 
	public void sdkLibTestCanLoadSrcTestParallelToTheSdkSrc() throws Exception {
		given(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).hasClass("brjsLib.Class1")
			.and(sdkLib).hasTestClasses("brjsLib.TestClass1")
			.and(sdkLibUTs).testRefersTo("pkg/test.js", "brjsLib.Class1", "brjsLib.TestClass1");
		then(sdkLibUTs).bundledFilesEquals(
				sdkLib.assetLocation("src").file("brjsLib/Class1.js"),
				sdkLib.assetLocation("src-test").file("brjsLib/TestClass1.js"));
	}
	
	@Ignore // TODO: test nodes should have their src test bundled
	@Test
	public void sdkLibTestCanLoadSrcTestInsideJsTestDriverFolder() throws Exception {
		given(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).hasClass("brjsLib.Class1")
			.and(sdkLibUTs).hasTestClasses("brjsLib.TestClass1")
			.and(sdkLibUTs).testRefersTo("pkg/test.js", "brjsLib.Class1", "brjsLib.TestClass1");
		then(sdkLibUTs).bundledFilesEquals(
			sdkLib.assetLocation("src").file("brjsLib/Class1.js"),
			sdkLibUTs.assetLocation("src-test").file("brjsLib/TestClass1.js"));
	}

}
