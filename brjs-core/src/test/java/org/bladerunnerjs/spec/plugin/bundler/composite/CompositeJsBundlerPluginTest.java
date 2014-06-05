package org.bladerunnerjs.spec.plugin.bundler.composite;

import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CompositeJsBundlerPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer requestResponse = new StringBuffer();
	private JsLib thirdpartyLib;
	private JsLib brLib;
	private JsLib appLib;
	private AliasesFile aspectAliasesFile;
	private JsLib brbootstrap;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			thirdpartyLib = app.jsLib("thirdparty-lib");
			aspectAliasesFile = aspect.aliasesFile();
			brLib = app.jsLib("br");
			brbootstrap = brjs.sdkLib("br-bootstrap");
			appLib = app.jsLib("appLib");
	}
	
	@Test
	public void thirdpartyAppearsFirstAndNamespacedModulesAppearLastInTheBundle() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns/node/NodeClass", "appns.namespaced.NamespacedClass")
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: src.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFile("src.js")
			.and(aspect).indexPageRefersTo("'thirdparty-lib', appns.namespaced.NamespacedClass, appns.node.NodeClass");
		when(aspect).requestReceived("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"// thirdparty-lib", 
				"module.exports = NodeClass",
				"appns.namespaced.NamespacedClass = function");
	}
	
	@Test
	public void theAliasBlobIsOutputLast() throws Exception {
		given(aspect).classRequires("appns/Class1", "br/AliasRegistry")
			.and(brLib).hasClass("br/AliasRegistry")
			.and(aspectAliasesFile).hasAlias("the-alias", "appns.Class1")
			.and(aspect).indexPageRefersTo("\"the-alias\"")
			.and(brbootstrap).containsFileWithContents("thirdparty-lib.manifest", "exports: lib")
			.and(brbootstrap).containsFile("bootstrap.js");
		when(aspect).requestReceived("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
			"// br-bootstrap",
			"define('appns/Class1'",
			"define('br/AliasRegistry'",
			"require('br/AliasRegistry').setAliasData(" );
	}
	
	@Test
	public void bundlesAreIncludedInTheRightOrder() throws Exception {
		given(aspect).hasNodeJsPackageStyle("src/appns/node")
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
					"require('appns.node.Class');\n" );
		when(aspect).requestReceived("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments(
				"// br-bootstrap", 
				"// appLib", 
				"define('appns/node/Class'", 
				"appns.namespaced.Class ="); 
	}
	
	@Test
	public void i18nBundleIsNotIncluded() throws Exception {
		given(aspect).hasNodeJsPackageStyle("src/appns/node")
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
					"require('appns.node.Class');\n" );
		when(aspect).requestReceived("js/dev/combined/bundle.js", requestResponse);
		then(requestResponse).doesNotContainText("window._brjsI18nProperties = [{");
	}
	
}
