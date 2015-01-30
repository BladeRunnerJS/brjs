package org.bladerunnerjs.spec.bundling.testpack;

import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class SdkLibraryTestPackBundlingTest extends SpecTest
{
	private JsLib sdkLib;
	private TestPack sdkLibUTs, sdkLibATs;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
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
		then(sdkLibUTs).bundledFilesEquals(sdkLib.file("src/brjsLib/Class1.js"));
	}
	
	@Test
	public void weBundleSdkLibFilesInATs() throws Exception {
		given(sdkLib).hasNamespacedJsPackageStyle()
		.and(sdkLib).hasClass("brjsLib.Class1")
		.and(sdkLibATs).testRefersTo("pkg/test.js", "brjsLib.Class1");
	then(sdkLibATs).bundledFilesEquals(sdkLib.file("src/brjsLib/Class1.js"));
	}
	
	@Test
	public void noExceptionsAreThrownIfTheSdkLibSrcFolderHasAHiddenFolder() throws Exception {
		given(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).hasClass("brjsLib.Class1")
			.and(sdkLib).containsFileWithContents("src/.svn/generatedSvnFile.txt", "generatedContent")
			.and(sdkLibUTs).testRefersTo("pkg/test.js", "brjsLib.Class1");
		then(sdkLibUTs).bundledFilesEquals(sdkLib.file("src/brjsLib/Class1.js"));
	}
	
	@Test 
	public void sdkLibTestCanLoadSrcTestParallelToTheSdkSrc() throws Exception {
		given(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).hasClass("brjsLib.Class1")
			.and(sdkLib).hasTestClasses("brjsLib.TestClass1")
			.and(sdkLibUTs).testRefersTo("pkg/test.js", "brjsLib.Class1", "brjsLib.TestClass1");
		then(sdkLibUTs).bundledFilesEquals(
				sdkLib.file("src/brjsLib/Class1.js"),
				sdkLib.file("src-test/brjsLib/TestClass1.js"));
	}
	
	@Test
	public void sdkLibTestCanLoadSrcTestFromTestTechFolder() throws Exception {
		given(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).hasClass("brjsLib.Class1")
			.and(sdkLibUTs).hasTestClasses("brjsLib.TestClass1")
			.and(sdkLibUTs).testRefersTo("pkg/test.js", "brjsLib.TestClass1", "brjsLib.Class1");
		then(sdkLibUTs).bundledFilesEquals(
			sdkLib.file("src/brjsLib/Class1.js"),
			sdkLibUTs.file("src-test/brjsLib/TestClass1.js"));
	}	
	
	
	// N O D E - J S
	@Test
	public void weCanGenerateABundleForJsLibTestPacks() throws Exception {
		given(sdkLib).hasCommonJsPackageStyle()
			.and(sdkLibUTs).hasClass("brjsLib/SdkClass")
			.and(sdkLibUTs).testRequires("test.js", "brjsLib/SdkClass");
		when(sdkLibUTs).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("define('brjsLib/SdkClass'");
	}
	
	@Test
	public void encapsulatedStyleSourceModulesAreGlobalizedIfTheyAreUsedWithinANamespacedTestSourceClass() throws Exception {	
		given(sdkLib).hasCommonJsPackageStyle()
			.and(sdkLib).hasTestClass("brjsLib/TestClass")
			.and(sdkLibUTs).hasNamespacedJsPackageStyle()			
			.and(sdkLibUTs).testFileHasContent("pkg/test.js", "new brjsLib.TestClass()");
		when(sdkLibUTs).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(sdkLibUTs).bundledFilesEquals( sdkLib.file("src-test/brjsLib/TestClass.js") )
			.and(response).containsText( "brjsLib.TestClass = require('brjsLib/TestClass');" );
	}
	
	@Test
	public void encapsulatedStyleSourceModulesAreGlobalizedIfTheyAreUsedWithinANamespacedSourceClass() throws Exception {	
		given(sdkLib).hasCommonJsPackageStyle()
			.and(sdkLib).hasClass("brjsLib/Class")
			.and(sdkLibUTs).hasNamespacedJsPackageStyle()			
			.and(sdkLibUTs).testRefersTo("pkg/test.js", "brjsLib/Class");
		when(sdkLibUTs).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(sdkLibUTs).bundledFilesEquals( sdkLib.file("src/brjsLib/Class.js") )
			.and(response).containsText( "brjsLib.Class = require('brjsLib/Class');" );
	}
	
	@Test
	public void encapsulatedStyleSourceModulesAreGlobalizedIfTheyAreUsedWithinATestTechnologyNamespacedTestSourceClass() throws Exception {	
		given(sdkLib).hasCommonJsPackageStyle()
    		.and(sdkLibUTs).hasTestClass("brjsLib/sdkLibUTs/Class")
    		.and(sdkLibUTs).hasNamespacedJsPackageStyle("tests")			
    		.and(sdkLibUTs).testFileHasContent("pkg/test.js", "new brjsLib.sdkLibUTs.Class()");
    	when(sdkLibUTs).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(sdkLibUTs).bundledFilesEquals( sdkLibUTs.file("src-test/brjsLib/sdkLibUTs/Class.js") )
    		.and(response).containsText( "brjsLib.sdkLibUTs.Class = require('brjsLib/sdkLibUTs/Class');" );
	}
	
}
