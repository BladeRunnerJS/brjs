package org.bladerunnerjs.spec.plugin.bundler.namespacedjs;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.TypedTestPack;
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
	private Bladeset bladeset;
	private Blade blade;
	private TypedTestPack bladeTestPack, sdkJsLibTestPack;
	private TestPack aspectTests, bladeTests, sdkJsLibTests;
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
			aspectTests = aspect.testType("unit").testTech("js-test-driver");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeTestPack = blade.testType("test");
			bladeTests = bladeTestPack.testTech("techy");
			thirdpartyLib = app.jsLib("lib1");
			sdkJsLib = brjs.sdkLib("sdkLib");
			bladerunnerConf = brjs.bladerunnerConf();
			sdkJsLibTestPack = sdkJsLib.testType("test");
			sdkJsLibTests = sdkJsLibTestPack.testTech("jsTestDriver");
			defaultBladeset = app.bladeset("default");
			bladeInDefaultBladeset = defaultBladeset.blade("b1");
	}
	
	@Test
	public void ifThereAreNoJsFilesThenNoRequestsWillBeGenerated() throws Exception {
		given(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAreEmpty("namespaced-js");
	}
	
	@Test
	public void ifThereAreJsFilesThenMultipleRequestsWillBeGeneratedInDev() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageRefersTo("appns.Class")
			.and(aspect).hasClass("appns.Class");
		then(aspect).devRequestsForContentPluginsAre("namespaced-js", "namespaced-js/package-definitions.js", "namespaced-js/module/appns/Class.js", "namespaced-js/globalize-extra-classes.js");
	}
	
	@Test
	public void ifThereAreJsFilesThenASingleBundleRequestWillBeGeneratedInProd() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageRefersTo("appns.Class")
			.and(aspect).hasClass("appns.Class");
		then(aspect).prodRequestsForContentPluginsAre("namespaced-js", "namespaced-js/bundle.js");
	}
	
	@Test
	public void theBundleIsEmptyIfWeDontReferToAnyOfTheClasses() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classDependsOn("appns.Class1", "appns.Class2");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void theBundleContainsClassesThatAreReferredTo() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classDependsOn("appns.Class1", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void referencesAreNotProcessedIfCommentedOutWithTwoSlashes() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.TheClass")
			.and(aspect).indexPageHasContent("// appns.TheClass");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void referencesAreNotProcessedIfCommentedOutWithWithHTMLStyleComments() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.TheClass")
			.and(aspect).indexPageHasContent("<!-- appns.TheClass -->");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void referencesAreNotProcessedIfCommentedOutWithSlashStar() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.TheClass")
			.and(aspect).indexPageHasContent("/* appns.TheClass */");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void referencesAreNotProcessedIfCommentedOutWithSlashStarStar() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.TheClass")
			.and(aspect).indexPageHasContent("/** appns.TheClass */");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void staticReferencesAreNotProcessedIfCommentedOutWithTwoSlashes() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1",
				"appns.Class1 = function() {};\n" +
				"// br.Core.extend(appns.Class1, appns.Class2);");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsClasses("appns.Class1")
			.and(requestResponse).doesNotContainClasses("appns.Class2");
	}
	
	@Test
	public void staticReferencesAreNotProcessedIfCommentedOutWithSlashStar() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1",
				"appns.Class1 = function() {};\n" +
				"/* br.Core.extend(appns.Class1, appns.Class2); */");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsClasses("appns.Class1")
			.and(requestResponse).doesNotContainClasses("appns.Class2");
	}
	
	@Test
	public void staticReferencesAreNotProcessedIfCommentedOutWithSlashSlashStar() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1",
				"appns.Class1 = function() {};\n" +
				"/** br.Core.extend(appns.Class1, appns.Class2); */");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsClasses("appns.Class1")
			.and(requestResponse).doesNotContainClasses("appns.Class2");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainSinglePackageIfThereIsOneTopLevelClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1");
		when(aspect).requestReceivedInDev("namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("mergePackageBlock(window, {\"appns\":{}});");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainSinglePackageIfThereAreTwoTopLevelClasses() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).classDependsOn("appns.Class1", "appns.Class2");
		when(aspect).requestReceivedInDev("namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("mergePackageBlock(window, {\"appns\":{}});");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldBeEmptyIfNoneOfTheClassesAreUsed() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspect).classDependsOn("appns.Class1", "appns.Class2");
		when(aspect).requestReceivedInDev("namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).isEmpty();
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldContainTwoPackagesIfThereAreClassesAtDifferentLevels() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.pkg.Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).classDependsOn("appns.Class1", "appns.pkg.Class2");
		when(aspect).requestReceivedInDev("namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("mergePackageBlock(window, {\"appns\":{\"pkg\":{}}});");
	}
	
	@Test
	public void thePackageDefinitionsBlockShouldntContainPackagesForTestClasses() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspectTests).hasClass("appns.pkg1.Class")
			.and(aspectTests).hasTestClass("appns.pkg2.TestClass")
			.and(aspectTests).testRefersTo("appns/pkg3/test.js", "appns.pkg1.Class", "appns.pkg2.TestClass");
		when(aspectTests).requestReceivedInDev("namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("mergePackageBlock(window, {\"appns\":{\"pkg1\":{},\"pkg2\":{}}});");
	}
	
	@Test
	public void eachClassShouldBeReturnedLargelyUnchanged() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/Class1.js", "appns.Class1 = function() {\n};");
		when(aspect).requestReceivedInDev("namespaced-js/module/appns/Class1.js", requestResponse);
		then(requestResponse).textEquals("define('appns/Class1', function(require, exports, module) {\nappns.Class1 = function() {\n};\nmodule.exports = appns.Class1;\n});\n");
	}
	
	@Test
	public void requiresAreAlsoAutomaticallyAddedWithinTheBundledResponse() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasCommonJsPackageStyle("src/appns/commonjs")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns/commonjs/Class")
			.and(aspect).indexPageRefersTo("appns.namespaced.Class")
			.and(aspect).classDependsOn("appns.namespaced.Class", "appns.commonjs.Class");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsText("appns.namespaced.Class = function() {\n};")
			.and(requestResponse).containsText("appns.commonjs.Class = require('appns/commonjs/Class');");
	}
	
	@Test
	public void autoGlobalisationReplacesInvalidCharacters() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasCommonJsPackageStyle("src/appns/common-js")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns/common-js/Class")
			.and(aspect).indexPageRefersTo("appns.namespaced.Class")
			.and(aspect).classDependsOn("appns.namespaced.Class", "appns.common-js.Class");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsText("appns.namespaced.Class = function() {\n};")
			.and(requestResponse).containsText("appns.common_js.Class = require('appns/common-js/Class');");
	}
	
	@Test
	public void requiresAreNotAutomaticallyAddedForThirdpartyLibrariesWhichAreNotEncapsulated() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.namespaced.Class")
			.and(aspect).indexPageRefersTo("appns.namespaced.Class")
			.and(aspect).classDependsOnThirdpartyLib("appns.namespaced.Class", thirdpartyLib)
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: lib.js\n"+"exports: thirdpartlib")
			.and(thirdpartyLib).containsFile("lib.js");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsClasses("appns.namespaced.Class")
			.and(requestResponse).doesNotContainText("require('lib1')");
	}
	
	@Test
	public void requiresAreOnlyAutomaticallyAddedOnceInABundleForAGivenClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns.namespaced.AnotherClass", "appns/commonjs/Class")
			.and(aspect).indexPageRefersTo("new appns.namespaced.Class(); new appns.namespaced.AnotherClass();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/Class.js", "new appns.commonjs.Class();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/AnotherClass.js", "new appns.commonjs.Class();");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsTextOnce("appns.commonjs.Class = require('appns/commonjs/Class');");
	}
	
	@Test
	public void requiresAreAddedForNamespacedJsClassesBeforeCommonJsClasses() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasCommonJsPackageStyle("src/appns/commonjs")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns/commonjs/Class")
			.and(aspect).indexPageRefersTo("appns.namespaced.Class")
			.and(aspect).classDependsOn("appns.namespaced.Class", "appns.commonjs.Class");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"appns.namespaced.Class = function() {\n};",
				"appns.namespaced.Class = require('appns/namespaced/Class');",
				"appns.commonjs.Class = require('appns/commonjs/Class');");
	}
	
	
	@Test
	public void packageDefinitionsInBundleContainAutomaticRequirePackages() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns.namespaced.AnotherClass", "appns/commonjs/Class")
			.and(aspect).indexPageRefersTo("new appns.namespaced.Class(); new appns.namespaced.AnotherClass();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/Class.js", "new appns.commonjs.Class();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/AnotherClass.js", "new appns.commonjs.Class();");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsTextOnce("mergePackageBlock(window, {\"appns\":{\"namespaced\":{},\"commonjs\":{}}});");
	}
	
	@Test
	public void packageDefinitionsContainsAutomaticRequirePackages() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns.namespaced.Class", "appns.namespaced.AnotherClass", "appns/commonjs/Class")
			.and(aspect).indexPageRefersTo("new appns.namespaced.Class(); new appns.namespaced.AnotherClass();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/Class.js", "new appns.commonjs.Class();")
			.and(aspect).containsFileWithContents("src/appns/namespaced/AnotherClass.js", "new appns.commonjs.Class();");
		when(aspect).requestReceivedInDev("namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsTextOnce("mergePackageBlock(window, {\"appns\":{\"namespaced\":{},\"commonjs\":{}}});");
	}
	
	@Test
	public void jsPatchesAreIncludedAfterTheSourceModuleAndInsideTheDefine() throws Exception {
		given(sdkJsLib).hasNamespacedJsPackageStyle("src")
			.and(sdkJsLib).hasClasses("sdkLib.Class")
			.and(aspect).indexPageRefersTo("new sdkLib.Class()")
			.and(brjs).containsFileWithContents("js-patches/sdkLib/Class.js", "sdkLib.Class.patch = function() {}");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"define('sdkLib/Class'",
				"sdkLib.Class = function()",
				"sdkLib.Class.patch = function() {}",
				"module.exports = sdkLib.Class;"
		);
	}
	
	@Test
	public void dependenciesInPatchesArePulledInToTheBundle() throws Exception {
		given(sdkJsLib).hasNamespacedJsPackageStyle("src")
			.and(sdkJsLib).hasClasses("sdkLib.Class1", "sdkLib.Class2")
			.and(aspect).indexPageRefersTo("new sdkLib.Class1()")
			.and(brjs).containsFileWithContents("js-patches/sdkLib/Class1.js", "new sdkLib.Class2()");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsClasses("sdkLib.Class2");
	}
	
	@Test
	public void weCanUseUTF8() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("UTF-8")
			.and().activeEncodingIs("UTF-8")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/Class1.js", "// $£€");
		when(aspect).requestReceivedInDev("namespaced-js/module/appns/Class1.js", requestResponse);
		then(requestResponse).containsText("$£€");
	}
	
	@Test
	public void weCanUseLatin1() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("ISO-8859-1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/Class1.js", "// $£");
		when(aspect).requestReceivedInDev("namespaced-js/module/appns/Class1.js", requestResponse);
		then(requestResponse).containsText("$£");
	}
	
	@Test
	public void weCanUseUnicodeFilesWithABomMarkerEvenWhenThisIsNotTheDefaultEncoding() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("UTF-16")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("src/appns/Class1.js", "// $£€");
		when(aspect).requestReceivedInDev("namespaced-js/module/appns/Class1.js", requestResponse);
		then(requestResponse).containsText("$£€");
	}
	
	@Test
	public void namespacedJsClassesAreWrappedInACommonJsDefineBlock() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespacedjs")
			.and(aspect).hasCommonJsPackageStyle("src/appns/commonjs")
			.and(aspect).hasClasses("appns.namespacedjs.Class1", "appns/commonjs/Class1", "appns/commonjs/Class2")
			.and(aspect).indexPageRefersTo("appns.namespacedjs.Class1")
			.and(aspect).classDependsOn("appns.namespacedjs.Class1", "appns.commonjs.Class1")
			.and(aspect).classRequires("appns/commonjs/Class1", "appns.commonjs.Class2");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"define('appns/namespacedjs/Class1', function(require, exports, module) {",
				"appns.namespacedjs.Class1 = function()",
				"module.exports = appns.namespacedjs.Class1;" 
				);
	}
	
	@Test
	public void staticDependenciesAreRequiredAtTheTopOfTheModuleDefinition() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespacedjs")
			.and(aspect).hasClasses("appns.namespacedjs.Class1", "appns.namespacedjs.Class2")
			.and(aspect).classStaticallyDependsOn("appns.namespacedjs.Class1", "appns.namespacedjs.Class2")
			.and(aspect).indexPageRefersTo("appns.namespacedjs.Class1");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"define('appns/namespacedjs/Class1', function(require, exports, module) { requireAll(['appns/namespacedjs/Class2']);",
				"appns.namespacedjs.Class1 = function()",
				"module.exports = appns.namespacedjs.Class1;");
	}
	
	@Test
	public void requiredStaticDependenciesOnlyIncludeSourceModules() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespacedjs")
			.and(aspect).hasClasses("appns.namespacedjs.Class1")
			.and(aspect).containsResourceFileWithContents("file.xml", "<rootElem xmlns=\"http://schema.acme.org/schema\"><elem id='appns.namespacedjs.SomeId'/</rootElem>")
			.and(aspect).classStaticallyDependsOn("appns.namespacedjs.Class1", "appns.namespacedjs.SomeId")
			.and(aspect).indexPageRefersTo("appns.namespacedjs.Class1");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).doesNotContainText("requireAll");
	}
	
	
	@Test
	public void packageDefinitionsIncludesClassesNotDirectlyUsedByANamespacedClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespacedjs")
    		.and(aspect).hasCommonJsPackageStyle("src/appns/commonjs")
    		.and(aspect).hasClasses("appns.namespacedjs.Class1", "appns/commonjs/Class1", "appns/commonjs/pkg/Class2")
    		.and(aspect).indexPageRefersTo("appns.namespacedjs.Class1")
    		.and(aspect).classDependsOn("appns.namespacedjs.Class1", "appns.commonjs.Class1")
    		.and(aspect).classRequires("appns/commonjs/Class1", "appns.commonjs.pkg.Class2");
		when(aspect).requestReceivedInDev("namespaced-js/package-definitions.js", requestResponse);
		then(requestResponse).containsText("mergePackageBlock(window, {\"appns\":{\"namespacedjs\":{},\"commonjs\":{\"pkg\":{}}}});");
	}
	
	@Test
	public void testClassesInABladeHaveTheCorrectGlobalizedPaths() throws Exception {
		given(blade).hasCommonJsPackageStyle()
			.and(blade).hasClasses("appns/bs/b1/Class1")
			.and(bladeTests).hasTestClass("appns/bs/b1/TestClass1")
			.and(bladeTests).hasNamespacedJsPackageStyle("tests")
			.and(bladeTests).testRefersTo("BladeTest.js", "appns.bs.b1.Class1", "appns.bs.b1.TestClass1");
		when(bladeTests).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsText( "appns.bs.b1.TestClass1 = require('appns/bs/b1/TestClass1');" );
	}
	
	@Test
	public void commonJsTestsInRootTestsDirInAnSdkLibDontHaveTheirPathsGlobalized() throws Exception {
		given(sdkJsLib).hasCommonJsPackageStyle()
    		.and(sdkJsLib).hasClasses("sdkLib/Class1")
    		.and(sdkJsLibTests).hasTestClass("sdkLib/TestClass1")
    		.and(sdkJsLibTests).containsFileWithContents("tests/LibTest.js", "new sdkLib.Class1(); new sdkLib.TestClass1();");
    	when(sdkJsLibTests).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
    	then(requestResponse).doesNotContainText( "sdkLib.LibTest = require" );
	}
	
	@Test
	public void commonJsTestsInRootTestsDirInAnSdkLibWithNestedRequirePrefixDontHaveTheirPathsGlobalized() throws Exception {
		given(sdkJsLib).hasCommonJsPackageStyle()
			.and(sdkJsLib).containsFileWithContents("br-lib.conf", "requirePrefix: sdkLib/subPkg")
    		.and(sdkJsLib).hasClasses("sdkLib/subPkg/Class1")
    		.and(sdkJsLibTests).hasTestClass("sdkLib/subPkg/TestClass1")
    		.and(sdkJsLibTests).containsFileWithContents("tests/LibTest.js", "new sdkLib.subPkg.Class1(); new sdkLib.subPkg.TestClass1();");
    	when(sdkJsLibTests).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
    	then(requestResponse).doesNotContainText( "sdkLib.subPkg.LibTest = require" );
	}
	
	@Test
	public void dependenciesOfStaticDependenciesAreIncludedInTheRightOrder() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.Class3")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1",
					"appns.Class1 = function() {};\n" +
					"appns.Class2();")
			.and(aspect).classFileHasContent("appns.Class2",
					"appns.Class2 = function() {\n" +
					"	appns.Class3();\n" +
					"};\n")
			.and(aspect).classFileHasContent("appns.Class3",
					"appns.Class3 = function() {};\n");
    	when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
    	then(requestResponse).containsOrderedTextFragments(
    			"appns.Class2 = function()",
    			"appns.Class3 = function()",
    			"appns.Class1 = function()");
	}
	
	@Test
	public void staticDependenciesOfStaticDependenciesAreIncludedInTheRightOrder() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
		.and(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.Class3")
		.and(aspect).indexPageRefersTo("appns.Class1")
		.and(aspect).classFileHasContent("appns.Class1",
				"appns.Class1 = function() {};\n" +
				"appns.Class2();")
				.and(aspect).classFileHasContent("appns.Class2",
						"appns.Class2 = function() {};\n" +
						"appns.Class3();\n")
						.and(aspect).classFileHasContent("appns.Class3",
								"appns.Class3 = function() {};\n");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"appns.Class3 = function()",
				"appns.Class2 = function()",
				"appns.Class1 = function()");
	}
	
	@Test
	public void selfExecutingFunctionsDontPreventCorrectCalculationOfStaticDependencies() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
		.and(aspect).hasClasses("appns.Class1", "appns.Class2")
		.and(aspect).indexPageRefersTo("appns.Class1")
		.and(aspect).classFileHasContent("appns.Class1",
				";(function() {\n" +
				"appns.Class1 = function() {};\n" +
				"appns.Class2();\n" +
				"});")
		.and(aspect).classFileHasContent("appns.Class2",
				"appns.Class2 = function() {};");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"appns.Class2 = function()",
				"appns.Class1 = function()");
	}
	
	@Test
	public void staticDependenciesAreAllPassedToTheRequireAllMethod() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
		.and(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.Class3")
		.and(aspect).indexPageRefersTo("appns.Class1")
		.and(aspect).classFileHasContent("appns.Class1",
				"appns.Class1 = function() {};\n" +
				"appns.Class2();\n" +
				"appns.Class3();")
		.and(aspect).classFileHasContent("appns.Class2",
				"appns.Class2 = function() {};")
		.and(aspect).classFileHasContent("appns.Class3",
				"appns.Class3 = function() {};\n");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"appns.Class2 = function()",
				"appns.Class3 = function()",
				"requireAll(['appns/Class2','appns/Class3']);",
				"appns.Class1 = function()");
	}
	
	@Test
	public void methodsWithTheSameNameAsALibraryDontIncludeTheLibrary() throws Exception {
		JsLib normalizeLib = app.jsLib("normalize");
		given(normalizeLib).containsFileWithContents("thirdparty-lib.manifest", "depends:")
			.and(normalizeLib).containsFileWithContents("script.js", "normalizeLib JS")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).classFileHasContent("appns.Class1", "e.normalize()");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).doesNotContainText("normalizeLib")
			.and(requestResponse).containsText("e.normalize()");
	}
	
	@Test
	public void bladeClassesInDefaultBladesetCanBeBundled() throws Exception {
		given(app).hasNamespacedJsPackageStyle()
			.and(bladeInDefaultBladeset).hasClass("appns.BladeClass")
			.and(aspect).indexPageRefersTo("appns.BladeClass");
		when(aspect).requestReceivedInDev("namespaced-js/bundle.js", requestResponse);
		then(requestResponse).containsClasses("appns.BladeClass");
	}
	
}
