package org.bladerunnerjs.spec.plugin.bundler;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsBundlerContentPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CompositeJsBundlerPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer pageResponse = new StringBuffer();
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
	
	//TODO: change the new-js.bundle back to js.bundle once the legacy js bundle tag handler is deleted
	
	@Test
	public void inDevSeparateJsFileRequestsAreGeneratedByDefault() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).indexPageHasContent("<@new-js.bundle@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("namespaced-js/package-definitions.js", "node-js/module/appns/Class1.js");
	}
	
	@Test
	public void inProdASingleBundlerRequestIsGeneratedByDefault() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).indexPageHasContent("<@new-js.bundle@/>");
		when(aspect).indexPageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsRequests("js/prod/en_GB/combined/bundle.js");
	}
	
	@Test
	public void noRequestPathsAreGeneratedInDevIfThereAreNoClasses() throws Exception {
		given(aspect).indexPageHasContent("<@new-js.bundle@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("namespaced-js/package-definitions.js");
	}
	
	@Test
	public void devMinifierAttributeCanAllowJsFilesToBeCombinedEvenInDev() throws Exception {
		given(aspect).indexPageHasContent("<@new-js.bundle dev-minifier='combined'@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("js/dev/en_GB/combined/bundle.js");
	}
	
	@Test
	public void prodMinifierAttributeCanAllowJsFilesToBeServedAsSeparateFiles() throws Exception {
		given(exceptions).arentCaught();
		
		given(aspect).hasClass("appns.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).indexPageHasContent("<@new-js.bundle prod-minifier='none'@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("namespaced-js/package-definitions.js", "node-js/module/appns/Class1.js");
	}
	
	@Test
	public void thirdpartyAppearsFirstAndNamespacedModulesAppearLastInTheBundle() throws Exception {
		given(aspect).hasPackageStyle("src/appns/namespaced", NamespacedJsBundlerContentPlugin.JS_STYLE)
			.and(aspect).hasClasses("appns.node.NodeClass", "appns.namespaced.NamespacedClass")
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "js: src.js")
			.and(thirdpartyLib).containsFile("src.js")
			.and(aspect).indexPageRefersTo("thirdparty-lib, appns.namespaced.NamespacedClass, appns.node.NodeClass");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", requestResponse);
		then(requestResponse).containsOrderedTextFragments("// thirdparty-lib", "module.exports = appns.node.NodeClass", "appns.namespaced.NamespacedClass = function");
	}
}
