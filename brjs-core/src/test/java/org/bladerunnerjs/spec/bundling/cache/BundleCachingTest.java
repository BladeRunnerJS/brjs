package org.bladerunnerjs.spec.bundling.cache;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BundleCachingTest extends SpecTest 
{
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private JsLib thirdpartyLib;

	private StringBuffer response = new StringBuffer();
	private SdkJsLib sdkJquery;
	private JsLib userJquery;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("widget");
			blade = bladeset.blade("time");
			
			thirdpartyLib = brjs.sdkNonBladeRunnerLib("thirdpartyLib");
			
			sdkJquery = brjs.sdkNonBladeRunnerLib("jquery");
			userJquery = app.jsLib("jquery");
	}
	
	// Cache tests should be irrespective of JS style (namespace/node)
	@Test
	public void weDoNotCacheIndexPageReferencesToSourceClasses() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).hasClass("appns/Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(app).hasReceivedRequst("/default-aspect/js/dev/combined/bundle.js");
		when(aspect).indexPageRefersTo("appns.Class2")
			.and(app).requestReceived("/default-aspect/js/dev/combined/bundle.js", response);
		then(response).containsNodeJsClasses("appns.Class2")
			.and(response).doesNotContainText("appns.Class1");
	}
	
	// TODO: find out why this breaks even though it's not because of caching?
	@Ignore
	@Test
	public void weDoNotCacheAspectSourceDependencies() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(app).hasReceivedRequst("/default-aspect/js/dev/combined/bundle.js");
		when(thirdpartyLib).populate()
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "js: file1.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFileWithContents("file1.js", "thirdpartyLib content")
			.and(aspect).classDependsOnThirdpartyLib("appns.Class1", thirdpartyLib)
			.and(app).requestReceived("/default-aspect/js/dev/combined/bundle.js", response);
		then(response).containsOrderedTextFragments(
				"// thirdpartyLib", 
				"thirdpartyLib content", 
				"mergePackageBlock(window, {\"appns\":{}});",
				"Class1 = function()",
				"module.exports = Class1");
	}
	
	@Test
	public void weDoNotCacheDependentSourceModulesInLinkedAssetFiles() throws Exception {
		given(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).resourceFileRefersTo("html/view.html", "appns.Class1")
			.and(app).hasReceivedRequst("/default-aspect/js/dev/combined/bundle.js");
		when(aspect).resourceFileRefersTo("html/view.html", "appns.Class2")
			.and(app).requestReceived("/default-aspect/js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("appns.Class1")
			.and(response).containsNodeJsClasses("appns.Class2");
	}
	
	@Test
	public void weDetectWhenResourceSubDirectoriesHaveNewResourcesAddedToBundle() throws Exception {
		given(aspect).resourceFileContains("html/view.html", "<div id='appns.view'>TESTCONTENT</div>")
			.and(app).hasReceivedRequst("/default-aspect/bundle.html");
		when(aspect).resourceFileContains("html/subdir/extradir/view.html", "<div id='appns.foo'>NEWCONTENT</div>")
			.and(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT")
			.and(response).containsText("NEWCONTENT");
	}
	
	@Test
	public void weDetectWhenExistingBladeHasNewClass() throws Exception {
		given(blade).classFileHasContent("appns/widget/time/Class1", "this is class1")
			.and(aspect).indexPageHasContent("require('appns/widget/time/Class1');")
			.and(app).hasReceivedRequst("/default-aspect/js/dev/combined/bundle.js");
		when(blade).containsFileWithContents("src/appns/widget/time/Class2.js", "this is class2")
			.and(aspect).indexPageHasContent(
					"require('appns/widget/time/Class1');\n" +
					"require('appns/widget/time/Class2');")
			.and(app).requestReceived("/default-aspect/js/dev/combined/bundle.js", response);
		then(response).containsText("this is class1")
			.and(response).containsText("this is class2");
	}
	
	@Test
	public void userThirdpartyLibraryIsLoadedInsteadOfSdkThirdpartyLibraryOnSecondRequest() throws Exception {
		given(sdkJquery).containsFileWithContents("library.manifest", "js: jquery.js" + "\n" + "exports: jquery")
			.and(sdkJquery).containsFileWithContents("jquery.js", "SDK jquery-content")
			.and(userJquery).containsFileWithContents("library.manifest", "js: jquery.js" + "\n" + "exports: null")
			.and(userJquery).containsFileWithContents("jquery.js", "USER jquery-content")
			.and(aspect).indexPageHasContent("require('jquery');")
			.and(app).hasReceivedRequst("/default-aspect/js/dev/combined/bundle.js");
		when(app).requestReceived("/default-aspect/js/dev/combined/bundle.js", response);
		then(response).containsText("USER jquery-content")
			.and(response).doesNotContainText("SDK jquery-content");
	}
	
}
