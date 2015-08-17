package org.bladerunnerjs.spec.plugin.bundler.composite;

import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.aliasesFile;

import java.io.File;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.spec.aliasing.AliasesFileBuilder;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class CompositeJsContentPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer requestResponse = new StringBuffer();
	private JsLib thirdpartyLib;
	private JsLib brLib;
	private JsLib appLib;
	private JsLib brbootstrap;
	private Aspect defaultAspect;
	private File targetDir;
	private AliasesFileBuilder aspectAliasesFileBuilder;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			defaultAspect = app.defaultAspect();
			thirdpartyLib = app.jsLib("thirdparty-lib");
			brLib = app.jsLib("br");
			brbootstrap = brjs.sdkLib("br-bootstrap");
			appLib = app.jsLib("appLib");
			targetDir = FileUtils.createTemporaryDirectory( this.getClass() );
			
			aspectAliasesFileBuilder = new AliasesFileBuilder(this, aliasesFile(aspect));
	}
	
	@Test
	public void ifThereAreNoJsFilesThenNoRequestsWillBeGenerated() throws Exception {
		given(aspect).indexPageHasContent("index page");
		then(aspect).prodAndDevRequestsForContentPluginsAreEmpty("js");
	}
	
	@Test
	public void ifThereAreJsFilesThenRequestsWillBeGeneratedInDev() throws Exception {
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).hasClass("appns/Class");
		then(aspect).devRequestsForContentPluginsAre("js", "js/dev/combined/bundle.js", "js/dev/closure-whitespace/bundle.js", "js/dev/closure-simple/bundle.js", "js/dev/closure-medium/bundle.js", "js/dev/closure-advanced/bundle.js");
	}
	
	@Test
	public void ifThereAreJsFilesThenRequestsWillBeGeneratedInProd() throws Exception {
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).hasClass("appns/Class");
		then(aspect).prodRequestsForContentPluginsAre("js", "js/prod/combined/bundle.js", "js/prod/closure-whitespace/bundle.js", "js/prod/closure-simple/bundle.js", "js/prod/closure-medium/bundle.js", "js/prod/closure-advanced/bundle.js");
	}
	
	@Test
	public void thirdpartyAppearsFirstAndNamespacedModulesAppearLastInTheBundle() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns/node/NodeClass", "appns.namespaced.NamespacedClass")
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: src.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFile("src.js")
			.and(aspect).indexPageRefersTo("'thirdparty-lib', appns.namespaced.NamespacedClass, appns.node.NodeClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"// thirdparty-lib",
				"module.exports = NodeClass",
				"appns.namespaced.NamespacedClass = function");
	}
	
	@Test
	public void theAliasBlobIsOutputAfterTheThirdpartyLibrariesButBeforeTheClasses() throws Exception {
		given(aspect).classRequires("appns/Class1", "br/AliasRegistry")
			.and(brLib).hasClass("br/AliasRegistry")
			.and(aspectAliasesFileBuilder).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageRefersTo("\"the-alias\"")
			.and(brbootstrap).containsFileWithContents("thirdparty-lib.manifest", "exports: lib")
			.and(brbootstrap).containsFile("bootstrap.js");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
			"// br-bootstrap",
			"define('br/AliasRegistry'",
			"define('appns/Class1'");
	}
	
	@Test
	public void bundlesAreIncludedInTheRightOrder() throws Exception {
		given(aspect).hasCommonJsPackageStyle("src/appns/node")
			.and(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClass("appns/node/Class")
			.and(aspect).hasClass("appns.namespaced.Class")
			.and(brbootstrap).containsFileWithContents("thirdparty-lib.manifest", "exports: lib")
			.and(brbootstrap).containsFile("bootstrap.js")
			.and(appLib).containsFileWithContents("thirdparty-lib.manifest", "exports: lib")
			.and(brbootstrap).containsFile("appLib.js")
			.and(aspect).indexPageHasContent("<@js.bundle@/>\n"+
					"appns.namespaced.Class\n"+
					"require('appLib');\n"+
					"require('appns/node/Class');\n" );
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragmentsAnyNumberOfTimes(
				"// br-bootstrap",
				"// appLib",
				"define('appns/node/Class'",
				"appns.namespaced.Class =");
	}
	
	@Test
	public void i18nBundleIsNotIncluded() throws Exception {
		given(aspect).hasCommonJsPackageStyle("src/appns/node")
			.and(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClass("appns/node/Class")
			.and(aspect).hasClass("appns.namespaced.Class")
			.and(brbootstrap).containsFileWithContents("thirdparty-lib.manifest", "exports: lib")
			.and(brbootstrap).containsFile("bootstrap.js")
			.and(appLib).containsFileWithContents("thirdparty-lib.manifest", "exports: lib")
			.and(brbootstrap).containsFile("appLib.js")
			.and(aspect).indexPageHasContent("<@js.bundle@/>\n"+
					"appns.namespaced.Class\n"+
					"require('appLib');\n"+
					"require('appns/node/Class');\n" );
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).doesNotContainText("window._brjsI18nProperties = [{");
	}
	
	@Test
	public void onlyMinifiersUsedFromATagHandlerAreReturnedAsUsedContentPaths() throws Exception {
		given(defaultAspect).indexPageHasContent("<@js.bundle dev-minifier='combined' prod-minifier='closure-whitespace' @/>\n"+"require('appns/Class');")
			.and(brjs).localeSwitcherHasContents("")
			.and(defaultAspect).hasClass("appns/Class")
			.and(brjs).hasVersion("1234");
		then(defaultAspect).usedProdContentPathsForPluginsAre("js", "js/prod/closure-whitespace/bundle.js");
	}
	
	@Test
	public void onlyMinifiersUsedFromATagHandlerArePresentInTheBuiltArtifact() throws Exception {
		given(defaultAspect).indexPageHasContent("<@js.bundle dev-minifier='combined'@/>\n"+"require('appns/Class');")
			.and(brjs).localeSwitcherHasContents("")
			.and(defaultAspect).hasClass("appns/Class")
			.and(brjs).hasVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("index.html", "v/1234/js/prod/combined/bundle.js")
			.and(targetDir).containsFile("v/1234/js/prod/combined/bundle.js")
			.and(targetDir).doesNotContainFile("v/1234/js/prod/closure-whitespace/bundle.js");
	}
	
	@Test
	public void aCommonJsClassWillAppearFirstInTheGlobalizationBlockIfItIsRequiredByAnotherClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespacedjs")
			.and(aspect).hasCommonJsPackageStyle("src/appns/commonjs")
			.and(aspect).hasClass("appns/commonjs/Class")
			.and(aspect).classExtends("appns.namespacedjs.Class", "appns.commonjs.Class")
			.and(aspect).indexPageRefersTo("appns.namespacedjs.Class");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
			"appns.commonjs.Class = require('appns/commonjs/Class');",
			"appns.namespacedjs.Class = require('appns/namespacedjs/Class');");
	}
	
	@Test
	public void aNamespacedJsClassWillAppearFirstInTheGlobalizationBlockIfItIsRequiredByAnotherClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespacedjs")
			.and(aspect).hasCommonJsPackageStyle("src/appns/commonjs")
			.and(aspect).hasClass("appns.namespacedjs.Class")
			.and(aspect).classRequires("appns/commonjs/Class", "appns/namespacedjs/Class")
			.and(aspect).indexPageRequires("appns/commonjs/Class");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
			"appns.namespacedjs.Class = require('appns/namespacedjs/Class');",
			"appns.commonjs.Class = require('appns/commonjs/Class');");
	}
	
}
