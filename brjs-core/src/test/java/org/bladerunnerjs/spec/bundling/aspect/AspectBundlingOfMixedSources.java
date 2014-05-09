package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AspectBundlingOfMixedSources extends SpecTest {
	private App app;
	private Aspect aspect, otherAspect;
	private JsLib sdkNamespaceLib, otherSdkNamespaceLib, sdkNodeJsLib, userLib, otherUserLib, sdkJquery, userJquery;
	private StringBuffer response = new StringBuffer();

	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
		app = brjs.app("app1");
		aspect = app.aspect("default");

		otherAspect = app.aspect("other");
		userLib = app.jsLib("userLib");
		otherUserLib = app.jsLib("otherUserLib");
		
		sdkNamespaceLib = brjs.sdkLib("sdkNamespaceLib");
		otherSdkNamespaceLib = brjs.sdkLib("otherSdkNamespaceLib");
		sdkNodeJsLib = brjs.sdkLib("sdkNodeJsLib");
		sdkJquery = brjs.sdkNonBladeRunnerLib("jquery");
		userJquery = app.jsLib("jquery");

		given(sdkNamespaceLib).hasNamespacedJsPackageStyle()
			.and(otherSdkNamespaceLib).hasNamespacedJsPackageStyle()
			.and(sdkNodeJsLib).hasNodeJsPackageStyle()
			.and(userLib).hasNodeJsPackageStyle()
			.and(otherUserLib).hasNodeJsPackageStyle();			
	}
	
	// Namespace and NodeJS styles together
	@Test
	public void testThatNamespaceStyleLibraryCanProxyToNodeJsClassAndGetBundledInAspectIndexPage() throws Exception
	{
		given(sdkNodeJsLib).classFileHasContent("sdkNodeJsLib.Class1", "function empty() {};")
			.and(sdkNamespaceLib).classFileHasContent("sdkNamespaceLib.ProxyClass", "sdkNamespaceLib.ProxyClass = sdkNodeJsLib.Class1;")
			.and(aspect).indexPageHasContent("require('sdkNamespaceLib/ProxyClass')");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsOrderedTextFragments(
				
				// The sdkNodeJsLib is defined
				"define('sdkNodeJsLib/Class1', function(require, exports, module) {",
				"function empty() {};",
				"});",
				// The classes are both made available globally
				"mergePackageBlock(window, {\"sdkNamespaceLib\":{},\"sdkNodeJsLib\":{}});",
				// The namespaced style sdk class is assigned to the require of the nodeJsLib class  
				"requireAll(['sdkNodeJsLib/Class1']);",
				"sdkNamespaceLib.ProxyClass = sdkNodeJsLib.Class1;",
				"sdkNodeJsLib.Class1 = require('sdkNodeJsLib/Class1');");
	}
	
	@Test
	public void testThatNamespaceStyleLibraryCanProxyToAnotherNamespaceLibraryClassAndGetBundledInAspectIndexPage() throws Exception
	{
		given(otherSdkNamespaceLib).classFileHasContent("otherSdkNamespaceLib.Class1", "otherSdkNamespaceLib.Class1 = function() { var x = 'original function'; };")
			.and(sdkNamespaceLib).classFileHasContent("sdkNamespaceLib.ProxyClass",
				"sdkNamespaceLib.ProxyClass = otherSdkNamespaceLib.Class1; function neverCalledButForcesLoadOrder() {caplin.extend(sdkNamespaceLib.ProxyClass, otherSdkNamespaceLib.Class1);}")
			.and(aspect).indexPageHasContent("require('sdkNamespaceLib/ProxyClass')");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsOrderedTextFragments(
				// globalise the libraries first
				"mergePackageBlock(window, {\"otherSdkNamespaceLib\":{},\"sdkNamespaceLib\":{}});",
				// The class being proxied (the depended-on class) should be added first
				"otherSdkNamespaceLib.Class1 = function() { var x = 'original function'; };",
				// The library class doing the proxying should be added next
				"sdkNamespaceLib.ProxyClass = otherSdkNamespaceLib.Class1;");
	}
	
	
	// dependencies across multiple aspects
	@Test
	public void canBundleDependenciesForAnotherAspectCorrectlyWithNodeJsLibsAndSdkNamespaceLib() throws Exception {
		given(sdkNamespaceLib).classFileHasContent("sdkNamespaceLib.Class1", "function empty() {};")
			.and(userLib).hasClass("userLib/Class1")
			.and(otherUserLib).hasClass("otherUserLib/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1", "sdkNodeJsLib.Class1")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).classRequires("appns/Class1", "userLib.Class1")
			.and(otherAspect).indexPageRefersTo("otherUserLib.Class1", "sdkNamespaceLib.Class1");
		when(app).requestReceived("/other-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsNodeJsClasses("otherUserLib.Class1")
			.and(response).doesNotContainText("userLib");
	}
	
	// user libraries depending on other libraries
	@Test
	public void userLibraryCanDependOnSdkThirdpartyLibrary() throws Exception {
		given(sdkJquery).containsFileWithContents("library.manifest", "js: jquery.js" + "\n" + "exports: jquery")
			.and(sdkJquery).containsFileWithContents("jquery.js", "jquery-content")
			.and(userLib).classFileHasContent("userLib.Class1", "require('jquery');")
			.and(aspect).indexPageHasContent("require('userLib.Class1');");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsDefinedClasses("jquery", "userLib/Class1");
	}
	
	@Test
	public void userLibraryCanDependOnSdkNodeJsLib() throws Exception {
		given(sdkNodeJsLib).classFileHasContent("sdkNodeJsLib.Class1", "function empty() {};")
			.and(userLib).classFileHasContent("userLib.Class1", "require('sdkNodeJsLib/Class1');")
			.and(aspect).indexPageHasContent("require('userLib.Class1');");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsDefinedClasses("sdkNodeJsLib/Class1", "userLib/Class1");
	}
	
	// TODO - this test does not bundle EITHER jquery libs if both have the same require path, comment either lib out to see results
	// User thirdparty lib in 'libs' dir overriding an sdk thirdparty library
	@Ignore
	@Test
	public void userThirdpartyLibraryIsLoadedInsteadOfSdkThirdpartyLibrary() throws Exception {
		given(sdkJquery).containsFileWithContents("library.manifest", "js: jquery.js" + "\n" + "exports: jquery")
			.and(sdkJquery).containsFileWithContents("jquery.js", "SDK jquery-content")
			.and(userJquery).containsFileWithContents("library.manifest", "js: jquery.js" + "\n" + "exports: null")
			.and(userJquery).containsFileWithContents("jquery.js", "USER jquery-content")
			.and(aspect).indexPageHasContent("require('jquery');");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("USER jquery-content")
			.and(response).doesNotContainText("SDK jquery-content");
	}
	
	// TODO - can be moved to caching tests once we have it working, it's convenient to have it in this Test Class for now
	@Ignore
	@Test
	public void userThirdpartyLibraryIsLoadedInsteadOfSdkThirdpartyLibraryOnSecondRequest() throws Exception {
		given(sdkJquery).containsFileWithContents("library.manifest", "js: jquery.js" + "\n" + "exports: jquery")
			.and(sdkJquery).containsFileWithContents("jquery.js", "SDK jquery-content")
			.and(userJquery).containsFileWithContents("library.manifest", "js: jquery.js" + "\n" + "exports: null")
			.and(userJquery).containsFileWithContents("jquery.js", "USER jquery-content")
			.and(aspect).indexPageHasContent("require('jquery');")
			.and(app).hasReceivedRequst("/default-aspect/js/dev/en_GB/combined/bundle.js");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("USER jquery-content")
			.and(response).doesNotContainText("SDK jquery-content");
	}
	
	// Legacy thirdparty-library overriding an sdk thirdparty library
	@Test
	public void userLegacyAppThirdpartyLibraryIsLoadedInsteadOfSdkThirdpartyLibrary() throws Exception {
		given(app).containsFileWithContents("thirdparty-libraries/jquery/library.manifest", "js: jquery.js" + "\n" + "exports: jquery")
			.and(app).containsFileWithContents("thirdparty-libraries/jquery/jquery.js", "LEGACY thirdparty jquery-content")
			.and(sdkJquery).containsFileWithContents("library.manifest", "js: jquery.js" + "\n" + "exports: jquery")
			.and(sdkJquery).containsFileWithContents("jquery.js", "SDK jquery-content")
			.and(aspect).indexPageHasContent("require('jquery');");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("LEGACY thirdparty jquery-content")
			.and(response).doesNotContainText("SDK jquery-content");
	}
}
