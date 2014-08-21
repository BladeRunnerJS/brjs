package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectBundlingOfMixedSources extends SpecTest {
	private App app;
	private Aspect aspect, otherAspect;
	private JsLib sdkNamespaceLib, otherSdkNamespaceLib, sdkCommonJsLib, userLib, otherUserLib, sdkJquery, userJquery;
	private StringBuffer response = new StringBuffer();
	private Bladeset bladeset;
	private Blade blade;

	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
		app = brjs.app("app1");
		aspect = app.aspect("default");
		bladeset = app.bladeset("bladeset");
		blade = bladeset.blade("blade");

		otherAspect = app.aspect("other");
		userLib = app.jsLib("userLib");
		otherUserLib = app.jsLib("otherUserLib");
		
		sdkNamespaceLib = brjs.sdkLib("sdkNamespaceLib");
		otherSdkNamespaceLib = brjs.sdkLib("otherSdkNamespaceLib");
		sdkCommonJsLib = brjs.sdkLib("sdkCommonJsLib");
		sdkJquery = brjs.sdkLib("jquery");
		userJquery = app.appJsLib("jquery");

		given(sdkNamespaceLib).hasNamespacedJsPackageStyle()
			.and(otherSdkNamespaceLib).hasNamespacedJsPackageStyle()
			.and(sdkCommonJsLib).hasCommonJsPackageStyle()
			.and(userLib).hasCommonJsPackageStyle()
			.and(otherUserLib).hasCommonJsPackageStyle();			
	}
	
	// Namespace and CommonJs styles together
	@Test
	public void testThatNamespaceStyleLibraryCanProxyToCommonJsClassAndGetBundledInAspectIndexPage() throws Exception
	{
		given(sdkCommonJsLib).classFileHasContent("sdkCommonJsLib.Class1", "function empty() {};")
			.and(sdkNamespaceLib).classFileHasContent("sdkNamespaceLib.ProxyClass", "sdkNamespaceLib.ProxyClass = sdkCommonJsLib.Class1;")
			.and(aspect).indexPageHasContent("require('sdkNamespaceLib/ProxyClass')");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsOrderedTextFragmentsAnyNumberOfTimes(
				
				// The sdkCommonJsLib is defined
				"define('sdkCommonJsLib/Class1', function(require, exports, module) {",
				"function empty() {};",
				"});",
				// The classes are both made available globally
				"mergePackageBlock(window, {\"sdkNamespaceLib\":{},\"sdkCommonJsLib\":{}});",
				// The namespaced style sdk class is assigned to the require of the CommonJsLib class  
				"requireAll(['sdkCommonJsLib/Class1']);",
				"sdkNamespaceLib.ProxyClass = sdkCommonJsLib.Class1;",
				"sdkCommonJsLib.Class1 = require('sdkCommonJsLib/Class1');");
	}
	
	@Test
	public void testThatNamespaceStyleLibraryCanProxyToAnotherNamespaceLibraryClassAndGetBundledInAspectIndexPage() throws Exception
	{
		given(otherSdkNamespaceLib).classFileHasContent("otherSdkNamespaceLib.Class1", "otherSdkNamespaceLib.Class1 = function() { var x = 'original function'; };")
			.and(sdkNamespaceLib).classFileHasContent("sdkNamespaceLib.ProxyClass",
				"sdkNamespaceLib.ProxyClass = otherSdkNamespaceLib.Class1; function neverCalledButForcesLoadOrder() {caplin.extend(sdkNamespaceLib.ProxyClass, otherSdkNamespaceLib.Class1);}")
			.and(aspect).indexPageHasContent("require('sdkNamespaceLib/ProxyClass')");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
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
	public void canBundleDependenciesForAnotherAspectCorrectlyWithCommonJsLibsAndSdkNamespaceLib() throws Exception {
		given(sdkNamespaceLib).classFileHasContent("sdkNamespaceLib.Class1", "function empty() {};")
			.and(userLib).hasClass("userLib/Class1")
			.and(otherUserLib).hasClass("otherUserLib/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1", "sdkCommonJsLib.Class1")
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).classRequires("appns/Class1", "userLib.Class1")
			.and(otherAspect).indexPageRefersTo("otherUserLib.Class1", "sdkNamespaceLib.Class1");
		when(otherAspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("otherUserLib.Class1")
			.and(response).doesNotContainText("userLib");
	}
	
	// user libraries depending on other libraries
	@Test
	public void userLibraryCanDependOnSdkThirdpartyLibrary() throws Exception {
		given(sdkJquery).containsFileWithContents("thirdparty-lib.manifest", "js: jquery.js" + "\n" + "exports: jquery")
			.and(sdkJquery).containsFileWithContents("jquery.js", "jquery-content")
			.and(userLib).classFileHasContent("userLib.Class1", "require('jquery');")
			.and(aspect).indexPageHasContent("require('userLib.Class1');");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("jquery", "userLib/Class1");
	}
	
	@Test
	public void userLibraryCanDependOnSdkCommonJsLib() throws Exception {
		given(sdkCommonJsLib).classFileHasContent("sdkCommonJsLib.Class1", "function empty() {};")
			.and(userLib).classFileHasContent("userLib.Class1", "require('sdkCommonJsLib/Class1');")
			.and(aspect).indexPageHasContent("require('userLib.Class1');");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("sdkCommonJsLib/Class1", "userLib/Class1");
	}
	
	// User thirdparty lib in 'libs' dir overriding an sdk thirdparty library
	@Test
	public void userThirdpartyLibraryIsLoadedInsteadOfSdkThirdpartyLibrary() throws Exception {
		given(sdkJquery).containsFileWithContents("thirdparty-lib.manifest", "js: jquery.js" + "\n" + "exports: jquery")
			.and(sdkJquery).containsFileWithContents("jquery.js", "SDK jquery-content")
			.and(userJquery).containsFileWithContents("thirdparty-lib.manifest", "js: jquery.js" + "\n" + "exports: null")
			.and(userJquery).containsFileWithContents("jquery.js", "USER jquery-content")
			.and(aspect).indexPageHasContent("require('jquery');");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("USER jquery-content")
			.and(response).doesNotContainText("SDK jquery-content");
	}
	
	@Test
	public void srcDirectoryFullPackageStructureIsOptionalForEveryNodeType() throws Exception {
		given(sdkNamespaceLib).hasNamespacedJsPackageStyle()
			.and(otherUserLib).hasNamespacedJsPackageStyle()
			.and(sdkNamespaceLib).hasClass("NamespacedLibClass")
			.and(otherUserLib).hasClass("OtherUserLibNamespacedClass")
			.and(sdkCommonJsLib).hasClass("CommonJSLibClass")
			.and(userLib).hasClass("UserLibCommonJSClass")
			.and(aspect).hasClass("AspectClass")
			.and(bladeset).hasClass("BladesetClass")
			.and(blade).hasClass("BladeClass")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("Class1",
					"require('sdkNamespaceLib/NamespacedLibClass');"+
					"require('otherUserLib/OtherUserLibNamespacedClass');"+
					"require('sdkCommonJsLib/CommonJSLibClass');"+
					"require('userLib/UserLibCommonJSClass');"+
					"require('./AspectClass');"+
					"require('./bladeset/BladesetClass');"+
					"require('./bladeset/blade/BladeClass');");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsNamespacedJsClasses("NamespacedLibClass", "OtherUserLibNamespacedClass")
			.and(response).containsCommonJsClasses("CommonJSLibClass", "UserLibCommonJSClass", "AspectClass", "BladesetClass", "BladeClass");
	}
	
	@Test
	public void exceptionIsThrownIfClassIsInIncorrectLocationAndSrcPathStartsWithAppRequirePrefix() throws Exception {
		given(aspect).classRequires("App", "appns/mypackage/Class")
			.and(aspect).indexPageRefersTo("appns.App")
			.and(bladeset).hasBeenCreated()
			.and(blade).hasClass("appns/mypkg/Class");
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(exceptions).verifyException(InvalidRequirePathException.class, "bladeset-bladeset/blades/blade/src/appns/mypkg/Class.js", "appns/bladeset/blade/*", "appns/mypkg");
	}
	
}
