package org.bladerunnerjs.spec.bundling.cache;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.SdkJsLib;
import org.junit.Before;
import org.junit.Test;

public class BundleCachingTest extends SpecTest 
{
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private JsLib thirdpartyLib;

	private StringBuffer initialResponse = new StringBuffer();
	private StringBuffer secondResponse = new StringBuffer();
	private StringBuffer response = new StringBuffer();
	private SdkJsLib sdkJquery;
	private JsLib userJquery;
	private JsLib library;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("widget");
			blade = bladeset.blade("time");
			
			thirdpartyLib = brjs.sdkLib("thirdpartyLib");
			
			sdkJquery = brjs.sdkLib("jquery");
			userJquery = app.appJsLib("jquery");
			library = app.appJsLib("lib");
	}
	
	// Cache tests should be irrespective of JS style (namespace/node)
	@Test
	public void weDoNotCacheIndexPageReferencesToSourceClasses() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).hasClass("appns/Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js");
		when(aspect).indexPageRefersTo("appns.Class2")
			.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class2")
			.and(response).doesNotContainText("appns.Class1");
	}
	
	@Test
	public void weDoNotCacheAspectSourceDependencies() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js");
		when(thirdpartyLib).create()
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: file1.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFileWithContents("file1.js", "thirdpartyLib content")
			.and(aspect).classDependsOnThirdpartyLib("appns.Class1", thirdpartyLib)
			.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsOrderedTextFragments(
				"// thirdpartyLib", 
				"thirdpartyLib content", 
				"mergePackageBlock(window, {\"appns\":{}});",
				"Class1 = function()",
				"module.exports = appns.Class1");
	}
	
	@Test
	public void weDoNotCacheDependentSourceModulesInLinkedAssetFiles() throws Exception {
		given(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.Class1")
			.and(aspect).hasReceivedRequest("js/dev/combined/bundle.js");
		when(aspect).resourceFileRefersTo("html/view.html", "appns.Class2")
			.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("appns.Class1")
			.and(response).containsCommonJsClasses("appns.Class2");
	}
	
	@Test
	public void weDetectWhenResourceSubDirectoriesHaveNewResourcesAddedToBundle() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div id='appns.view'>TESTCONTENT</div>")
			.and(aspect).hasReceivedRequest("html/bundle.html");
		when(aspect).resourceFileContains("html/subdir/extradir/view.html", "<div id='appns.foo'>NEWCONTENT</div>")
			.and(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("TESTCONTENT")
			.and(response).containsText("NEWCONTENT");
	}
	
	@Test
	public void weDetectWhenExistingBladeHasNewClass() throws Exception {
		given(blade).classFileHasContent("appns/widget/time/Class1", "this is class1")
			.and(aspect).indexPageHasContent("require('appns/widget/time/Class1');")
			.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js");
		when(blade).containsFileWithContents("src/appns/widget/time/Class2.js", "this is class2")
			.and(aspect).indexPageHasContent(
					"require('appns/widget/time/Class1');\n" +
					"require('appns/widget/time/Class2');")
			.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("this is class1")
			.and(response).containsText("this is class2");
	}
	
	@Test
	public void userThirdpartyLibraryIsLoadedInsteadOfSdkThirdpartyLibraryOnSecondRequest() throws Exception {
		given(sdkJquery).containsFileWithContents("thirdparty-lib.manifest", "js: jquery.js" + "\n" + "exports: jquery")
			.and(sdkJquery).containsFileWithContents("jquery.js", "SDK jquery-content")
			.and(userJquery).containsFileWithContents("thirdparty-lib.manifest", "js: jquery.js" + "\n" + "exports: null")
			.and(userJquery).containsFileWithContents("jquery.js", "USER jquery-content")
			.and(aspect).indexPageHasContent("require('jquery');")
			.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("USER jquery-content")
			.and(response).doesNotContainText("SDK jquery-content");
	}
	
	public void sourceFilesAreReadAsCorrectTypeIfJsStyleChangesFromNamespacedJsDuringRuntime() throws Exception {
		given(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).classFileHasContent("Class1", "require('./CommonJSClass'); appns.NamespacedJSClass();")
			.and(aspect).hasClasses("CommonJSClass", "NamespacedJSClass")
			.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js");
		when(aspect).hasCommonJsPackageStyle()
			.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("CommonJSClass = ");
	}
	
	@Test
	public void sourceFilesAreReadAsCorrectTypeIfJsStyleChangesFromCommonJsDuringRuntime() throws Exception {
		given(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).hasCommonJsPackageStyle()
			.and(aspect).classFileHasContent("Class1", "require('./CommonJSClass'); appns.NamespacedJSClass();")
			.and(aspect).hasClasses("CommonJSClass", "NamespacedJSClass")
			.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js");
		when(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("NamespacedJSClass = ");
	}
	
	@Test
	public void jsStyleCanChangeFromNamespacedJsDuringRuntime() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).hasClass("appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "appns.Class2();")
			.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js", initialResponse);
		when(aspect).hasCommonJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/Class1.js", "require('./Class2');")
			.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", secondResponse);
		then(initialResponse).containsText("appns.Class2 = ")
			.and(initialResponse).containsText("mergePackageBlock")
			.and(secondResponse).containsText("Class2 = ")
			.and(secondResponse).doesNotContainText("mergePackageBlock")
			.and(secondResponse).doesNotContainText("appns.Class2 = require(");
	}
	
	@Test
	public void jsStyleCanChangeFromCommonJsDuringRuntime() throws Exception {
		given(aspect).hasCommonJsPackageStyle()
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).hasClass("appns/Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "require('./Class2');")
			.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js", initialResponse);
		when(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class2();")
			.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", secondResponse);
		then(initialResponse).doesNotContainText("mergePackageBlock")
			.and(initialResponse).doesNotContainText("appns.Class2 =")
			.and(secondResponse).containsText("mergePackageBlock(window, {\"appns\":{}});")
			.and(secondResponse).containsText("appns.Class2 =");
	}
	
	@Test
	public void jsStyleCanChangeFromNamespacedJsInALibraryDuringRuntime() throws Exception {
		given(aspect).hasCommonJsPackageStyle()
    		.and(library).hasBeenCreated()
    		.and(library).hasCommonJsPackageStyle()
    		.and(library).hasClass("lib/Lib")
    		.and(aspect).classFileHasContent("appns.Class1", "require('lib/Lib');")
    		.and(aspect).indexPageRefersTo("appns.Class1")
    		.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js", initialResponse);
    	when(library).hasNamespacedJsPackageStyle()
    		.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", secondResponse);
    	then(initialResponse).doesNotContainText("mergePackageBlock")
    		.and(initialResponse).doesNotContainText("lib.Lib =")
    		.and(secondResponse).containsText("mergePackageBlock(window, {\"lib\":{},\"appns\":{}});")
    		.and(secondResponse).containsText("lib.Lib =");
	}
	
	@Test
	public void jsStyleCanChangeFromCommonJsInALibraryDuringRuntime() throws Exception {
		given(aspect).hasCommonJsPackageStyle()
    		.and(library).hasBeenCreated()
    		.and(library).hasNamespacedJsPackageStyle()
    		.and(library).hasClass("lib.Lib")
    		.and(aspect).classFileHasContent("appns.Class1", "require('lib/Lib');")
    		.and(aspect).indexPageRefersTo("appns.Class1")
    		.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js", initialResponse);
    	when(library).hasCommonJsPackageStyle()
    		.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", secondResponse);
		then(initialResponse).containsText("mergePackageBlock(window, {\"lib\":{},\"appns\":{}});")
    		.and(initialResponse).containsText("lib.Lib =")
    		.and(secondResponse).doesNotContainText("mergePackageBlock")
    		.and(secondResponse).doesNotContainText("lib.Lib = require(");
	}
	
	@Test
	public void jsStyleChangesAreProperlyDetectedIfTheFileChangesDirectlyOnDisk() throws Exception {
		given(aspect).hasCommonJsPackageStyle()
    		.and(library).hasBeenCreated()
    		.and(library).hasNamespacedJsPackageStyle()
    		.and(library).hasClass("lib.Lib")
    		.and(aspect).classFileHasContent("appns.Class1", "require('lib/Lib');")
    		.and(aspect).indexPageRefersTo("appns.Class1")
    		.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js", initialResponse);
    	when(library).containsFileWithContents(".js-style", "common-js");
    		library.file(".js-style").incrementFileVersion(); // do not use the 'hasCommonJsPackageStyle' spec test method here, we need to mimic what the file watcher thread does
    		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", secondResponse);
    	then(initialResponse).containsText("mergePackageBlock(window, {\"lib\":{},\"appns\":{}});")
    		.and(initialResponse).containsText("lib.Lib =")
    		.and(secondResponse).doesNotContainText("mergePackageBlock")
    		.and(secondResponse).doesNotContainText("lib.Lib = require(");
	}
	
}
