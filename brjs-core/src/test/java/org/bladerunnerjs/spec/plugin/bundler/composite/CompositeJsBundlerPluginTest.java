package org.bladerunnerjs.spec.plugin.bundler.composite;

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
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			thirdpartyLib = app.jsLib("thirdparty-lib");
	}
	
	@Test
	public void thirdpartyAppearsFirstAndNamespacedModulesAppearLastInTheBundle() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClasses("appns.node.NodeClass", "appns.namespaced.NamespacedClass")
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "js: src.js")
			.and(thirdpartyLib).containsFile("src.js")
			.and(aspect).indexPageRefersTo("thirdparty-lib, appns.namespaced.NamespacedClass, appns.node.NodeClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments("// thirdparty-lib", "module.exports = appns.node.NodeClass", "appns.namespaced.NamespacedClass = function");
	}
}
