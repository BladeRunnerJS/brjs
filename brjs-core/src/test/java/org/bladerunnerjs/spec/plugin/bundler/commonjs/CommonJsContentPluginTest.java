package org.bladerunnerjs.spec.plugin.bundler.commonjs;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CommonJsContentPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer requestResponse = new StringBuffer();
	private JsLib sdkJsLib;
	private Bladeset bladeset;
	private Blade blade;
	private Bladeset defaultBladeset;
	private Blade bladeInDefaultBladeset;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			sdkJsLib = brjs.sdkLib("sdkLib");
			defaultBladeset = app.defaultBladeset();
			bladeInDefaultBladeset = defaultBladeset.blade("b1");
	}
	
	@Test
	public void ifThereAreNoJsFilesThenNoRequestsWillBeGenerated() throws Exception {
		given(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAreEmpty("common-js");
	}
	
	@Test
	public void ifThereAreJsFilesThenMultipleRequestsWillBeGeneratedInDev() throws Exception {
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).hasClass("appns/Class");
		then(aspect).devRequestsForContentPluginsAre("common-js", "common-js/module/appns/Class.js");
	}
	
	@Test
	public void ifThereAreJsFilesThenASingleBundleRequestWillBeGeneratedInProd() throws Exception {
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).hasClass("appns/Class");
		then(aspect).prodRequestsForContentPluginsAre("common-js", "common-js/bundle.js");
	}
	
	@Test
	public void classesAreAutomaticallyWrappedInAClosure() throws Exception {
		given(aspect).hasClasses("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("common-js/module/appns/Class1.js", requestResponse);
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
		when(aspect).requestReceivedInDev("common-js/module/sdkLib/Class1.js", requestResponse);
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
		when(aspect).requestReceivedInDev("common-js/bundle.js", requestResponse);
		then(requestResponse).containsText("define('sdkLib/Class2'");
	}
	
	@Test 
	public void unresolvableRequirePathExceptionContainsFilePath() throws Exception{
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).classFileHasContent("appns/Class", "require('randomStuff')");
		when(aspect).requestReceivedInDev("common-js/bundle.js", requestResponse);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "randomStuff", "appns/Class" );
	}
	
	@Test 
	public void ambiguousRequirePathExceptionThrownFromIndexContainsFilePath() throws Exception{
		given(aspect).indexPageRequires("appns/bs/b1/Class")
			.and(aspect).hasClass("appns/bs/b1/Class")
			.and(bladeset).hasBeenCreated() 
			.and(blade).hasClass("appns/bs/b1/Class");
		when(aspect).requestReceivedInDev("common-js/bundle.js", requestResponse);
		then(exceptions).verifyException(AmbiguousRequirePathException.class, 	"default-aspect/index.html",
																					"bs-bladeset/blades/b1/src/appns/bs/b1/Class.js",
																					"default-aspect/src/appns/bs/b1/Class.js",
																					"appns/bs/b1/Class");
	}
	
	@Test 
	public void ambiguousRequirePathExceptionThrownFromSourceModuleContainsFilePath() throws Exception{
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).classRequires("appns/Class", "appns/bs/b1/Class")
			.and(aspect).hasClass("appns/bs/b1/Class")
			.and(bladeset).hasBeenCreated() 
			.and(blade).hasClass("appns/bs/b1/Class");
		when(aspect).requestReceivedInDev("common-js/bundle.js", requestResponse);
		then(exceptions).verifyException(AmbiguousRequirePathException.class, 	"appns/Class",
																					"bs-bladeset/blades/b1/src/appns/bs/b1/Class.js",
																					"default-aspect/src/appns/bs/b1/Class.js",
																					"appns/bs/b1/Class");
	}
	
	@Test
	public void bladeClassesInDefaultBladesetCanBeBundled() throws Exception {
		given(bladeInDefaultBladeset).hasClass("appns/b1/BladeClass")
			.and(aspect).indexPageRequires("appns/b1/BladeClass");
		when(aspect).requestReceivedInDev("common-js/bundle.js", requestResponse);
		then(requestResponse).containsCommonJsClasses("appns/b1/BladeClass");
	}
	
}
