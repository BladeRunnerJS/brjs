package org.bladerunnerjs.spec.plugin.bundler.commonjs;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CommonJsContentPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer requestResponse = new StringBuffer();
	private JsLib sdkJsLib;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			sdkJsLib = brjs.sdkLib("sdkLib");
	}
	
	@Test
	public void ifThereAreNoJsFilesThenNoRequestsWillBeGenerated() throws Exception {
		given(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAreEmpty("node-js");
	}
	
	@Test
	public void ifThereAreJsFilesThenMultipleRequestsWillBeGeneratedInDev() throws Exception {
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).hasClass("appns/Class");
		then(aspect).devRequestsForContentPluginsAre("node-js", "node-js/module/appns/Class.js");
	}
	
	@Test
	public void ifThereAreJsFilesThenASingleBundleRequestWillBeGeneratedInProd() throws Exception {
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).hasClass("appns/Class");
		then(aspect).prodRequestsForContentPluginsAre("node-js", "node-js/bundle.js");
	}
	
	@Test
	public void classesAreAutomaticallyWrappedInAClosure() throws Exception {
		given(aspect).hasClasses("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("node-js/module/appns/Class1.js", requestResponse);
		then(requestResponse).containsLines(
			"define('appns/Class1', function(require, exports, module) {",
			"Class1 = function() {",
			"};\n",
			"module.exports = Class1;\n",
			"});");
	}
	
	@Test
	public void jsPatchesAreIncludedInTheClosure() throws Exception {
		given(sdkJsLib).hasClasses("sdkLib/Class1")
			.and(aspect).indexPageRefersTo("sdkLib.Class1")
			.and(brjs).containsFileWithContents("js-patches/sdkLib/Class1.js", "sdkLib.Class1.patch = function() {}");
		when(aspect).requestReceivedInDev("node-js/module/sdkLib/Class1.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
			"define('sdkLib/Class1', function(require, exports, module) {",
			"Class1 = function() {",
			"};",
			"module.exports = Class1;",
			"sdkLib.Class1.patch = function() {}",
			"\n});");
	}
	
	@Test
	public void requiresInPatchesArePulledInToTheBundle() throws Exception {
		given(sdkJsLib).hasClasses("sdkLib/Class1", "sdkLib/Class2")
			.and(aspect).indexPageRefersTo("sdkLib.Class1")
			.and(brjs).containsFileWithContents("js-patches/sdkLib/Class1.js", "require('sdkLib/Class2')");
		when(aspect).requestReceivedInDev("node-js/bundle.js", requestResponse);
		then(requestResponse).containsText("define('sdkLib/Class2'");
	}
	
	@Test 
	public void sourceModuleExceptionContainsFilePath() throws Exception{
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).classFileHasContent("appns/Class", "require('randomStuff')");
		when(aspect).requestReceivedInDev("node-js/bundle.js", requestResponse);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "randomStuff", "appns/Class.js" );
	}
	
}
