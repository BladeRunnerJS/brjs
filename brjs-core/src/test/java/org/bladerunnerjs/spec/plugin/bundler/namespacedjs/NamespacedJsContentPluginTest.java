package org.bladerunnerjs.spec.plugin.bundler.namespacedjs;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class NamespacedJsContentPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer requestResponse = new StringBuffer();
	private JsLib thirdpartyLib;
	private JsLib sdkJsLib;
	private BladerunnerConf bladerunnerConf;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			thirdpartyLib = app.jsLib("lib1");
			sdkJsLib = brjs.sdkLib("sdkLib");
			bladerunnerConf = brjs.bladerunnerConf();
	}
	
	@Test
	public void theBundleIsEmptyIfWeDontReferToAnyOfTheClasses() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classRefersTo("appns.Class1", "appns.Class2");
		when(app).requestReceived("/default-aspect/namespaced-js/bundle.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainSinglePackageIfThereIsOneTopLevelClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1");
		when(app).requestReceived("/default-aspect/namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.appns = {};");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainSinglePackageIfThereAreTwoTopLevelClasses() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).classRefersTo("appns.Class1", "appns.Class2");
		when(app).requestReceived("/default-aspect/namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.appns = {};");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldBeEmptyIfNoneOfTheClassesAreUsed() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classRefersTo("appns.Class1", "appns.Class2");
		when(app).requestReceived("/default-aspect/namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainTwoPackagesIfThereAreClassesAtDifferentLevels() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.pkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).classRefersTo("appns.Class1", "appns.pkg.Class2");
		when(app).requestReceived("/default-aspect/namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("window.appns = {\"pkg\":{}};");
	}
	
	@Test
	public void eachClassShouldBeReturnedLargelyUnchanged() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function() {\n};");
		when(app).requestReceived("/default-aspect/namespaced-js/module/appns/Class1.js", requestResponse);
		then(requestResponse).textEquals("appns.Class1 = function() {\n};\ndefine('appns/Class1', function(require, exports, module) { module.exports = appns.Class1; });");
	}
	
	@Test
	public void caplinStyleClassesThatReferToRequireEncapsulatedClassesWillHaveRequiresAutomaticallyAdded() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns.nodejs.Class")
			.and(aspect).classRefersTo("appns.namespaced.Class", "appns.nodejs.Class");
		when(app).requestReceived("/default-aspect/namespaced-js/module/appns/namespaced/Class.js", requestResponse);
		then(requestResponse).containsText("appns.namespaced.Class = function() {\n};")
			.and(requestResponse).containsTextOnce("appns.nodejs.Class = require('appns/nodejs/Class');");
	}
	
	@Test
	public void requiresAreAlsoAutomaticallyAddedWithinTheBundledResponse() throws Exception {
		given(exceptions).arentCaught();
		
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns.nodejs.Class")
			.and(aspect).indexPageRefersTo("appns.namespaced.Class")
			.and(aspect).classRefersTo("appns.namespaced.Class", "appns.nodejs.Class");
		when(app).requestReceived("/default-aspect/namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsText("appns.namespaced.Class = function() {\n};")
			.and(requestResponse).containsText("appns.nodejs.Class = require('appns/nodejs/Class');");
	}
	
	@Test
	public void requiresAreNotAutomaticallyAddedForThirdpartyLibrariesWhichAreNotEncapsulated() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.namespaced.Class")
			.and(aspect).indexPageRefersTo("appns.namespaced.Class")
			.and(aspect).classRefersToThirdpartyLib("appns.namespaced.Class", thirdpartyLib)
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "js: lib.js\n"+"exports: thirdpartlib")
			.and(thirdpartyLib).containsFile("lib.js");
		when(app).requestReceived("/default-aspect/namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsClasses("appns.namespaced.Class")
			.and(requestResponse).doesNotContainText("require('");
	}
	
	@Test
	public void requiresAreOnlyAutomaticallyAddedOnceInABundleForAGivenClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns.namespaced.AnotherClass", "appns.nodejs.Class")
			.and(aspect).indexPageRefersTo("new appns.namespaced.Class(); new appns.namespaced.AnotherClass();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/Class.js", "new appns.nodejs.Class();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/AnotherClass.js", "new appns.nodejs.Class();");
		when(app).requestReceived("/default-aspect/namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsTextOnce("appns.nodejs.Class = require('appns/nodejs/Class');");
	}
	
	@Test
	public void automaticRequiresAreAddedOnlyOnceEvenIfTheClassIsReferredToMultipleTimes() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns.nodejs.Class")
			.and(aspect).classFileHasContent("appns.namespaced.Class", "appns.nodejs.Class, appns.nodejs.Class");
		when(app).requestReceived("/default-aspect/namespaced-js/module/appns/namespaced/Class.js", requestResponse);
		then(requestResponse).containsTextOnce("appns.nodejs.Class = require('appns/nodejs/Class');");
	}
	
	@Test
	public void packageDefinitionsInBundleContainAutomaticRequirePackages() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns.namespaced.AnotherClass", "appns.nodejs.Class")
			.and(aspect).indexPageRefersTo("new appns.namespaced.Class(); new appns.namespaced.AnotherClass();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/Class.js", "new appns.nodejs.Class();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/AnotherClass.js", "new appns.nodejs.Class();");
		when(app).requestReceived("/default-aspect/namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsTextOnce("window.appns = {\"nodejs\":{},\"namespaced\":{}};");
	}
	
	@Test
	public void packageDefinitionsContainsAutomaticRequirePackages() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns.namespaced.AnotherClass", "appns.nodejs.Class")
			.and(aspect).indexPageRefersTo("new appns.namespaced.Class(); new appns.namespaced.AnotherClass();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/Class.js", "new appns.nodejs.Class();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/AnotherClass.js", "new appns.nodejs.Class();");
		when(app).requestReceived("/default-aspect/namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsTextOnce("window.appns = {\"nodejs\":{},\"namespaced\":{}};");
	}
	
	@Test
	public void jsPatchesAreIncludedAfterTheSourceModule() throws Exception {
		given(sdkJsLib).hasNamespacedJsPackageStyle("src")
			.and(sdkJsLib).hasClasses("sdkLib.Class")
			.and(aspect).indexPageRefersTo("new sdkLib.Class()")
			.and(brjs).containsFileWithContents("js-patches/sdkLib/Class.js", "sdkLib.Class.patch = function() {}");
		when(app).requestReceived("/default-aspect/namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"sdkLib.Class = function()",
				"sdkLib.Class.patch = function() {}"
		);
	}
	
	@Test
	public void dependenciesInPatchesArePulledInToTheBundle() throws Exception {
		given(sdkJsLib).hasNamespacedJsPackageStyle("src")
			.and(sdkJsLib).hasClasses("sdkLib.Class1", "sdkLib.Class2")
			.and(aspect).indexPageRefersTo("new sdkLib.Class1()")
			.and(brjs).containsFileWithContents("js-patches/sdkLib/Class1.js", "new sdkLib.Class2()");
		when(app).requestReceived("/default-aspect/namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsClasses("sdkLib.Class2");
	}
	
	@Test
	public void weCanUseUTF8() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("UTF-8")
			.and().activeEncodingIs("UTF-8")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/Class1.js", "// $£€");
		when(app).requestReceived("/default-aspect/namespaced-js/module/appns/Class1.js", requestResponse);
		then(requestResponse).containsText("$£€");
	}
	
	@Test
	public void weCanUseLatin1() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("ISO-8859-1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/Class1.js", "// $£");
		when(app).requestReceived("/default-aspect/namespaced-js/module/appns/Class1.js", requestResponse);
		then(requestResponse).containsText("$£");
	}
	
	@Test
	public void weCanUseUnicodeFilesWithABomMarkerEvenWhenThisIsNotTheDefaultEncoding() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("UTF-16")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/Class1.js", "// $£€");
		when(app).requestReceived("/default-aspect/namespaced-js/module/appns/Class1.js", requestResponse);
		then(requestResponse).containsText("$£€");
	}
	
	@Test
	public void weGlobalizeNonNamespaceClassesBeforeTheClassThatNeedsThemAndGlobalizeExtraClassesAtTheEnd() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespacedjs")
			.and(aspect).hasNodeJsPackageStyle("src/appns/nodejs")
			.and(aspect).hasClasses("appns.namespacedjs.Class1", "appns.nodejs.Class1", "appns.nodejs.Class2")
			.and(aspect).indexPageRefersTo("appns.namespacedjs.Class1")
			.and(aspect).classRefersTo("appns.namespacedjs.Class1", "appns.nodejs.Class1")
			.and(aspect).classRequires("appns.nodejs.Class1", "appns.nodejs.Class2");
		when(app).requestReceived("/default-aspect/namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"window.appns = {\"nodejs\":{},\"namespacedjs\":{}};",
				"appns.nodejs.Class1 = require('appns/nodejs/Class1');",
				"appns.namespacedjs.Class1 = function()",
				"define('appns/namespacedjs/Class1', function(require, exports, module) { module.exports = appns.namespacedjs.Class1;",
				"appns.nodejs.Class2 = require('appns/nodejs/Class2');");
	}
	
}
