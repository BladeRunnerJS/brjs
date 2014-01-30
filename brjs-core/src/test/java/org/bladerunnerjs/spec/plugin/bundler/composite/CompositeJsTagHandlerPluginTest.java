package org.bladerunnerjs.spec.plugin.bundler.composite;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class CompositeJsTagHandlerPluginTest extends SpecTest
{
	private App app;
	private Aspect aspect;
	private JsLib appLib;
	private JsLib brbootstrap;
	private StringBuffer pageResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			appLib = app.nonBladeRunnerLib("appLib");
			brbootstrap = brjs.sdkNonBladeRunnerLib("br-bootstrap");
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
		given(aspect).hasClass("appns.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).indexPageHasContent("<@new-js.bundle prod-minifier='none'@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("namespaced-js/package-definitions.js", "node-js/module/appns/Class1.js");
	}
	
	@Test
	public void seperateScriptTagsAreGeneratedInTheCorrectOrder() throws Exception {
		given(aspect).hasNodeJsPackageStyle("src/appns/node")
			.and(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClass("appns.node.Class")
			.and(aspect).hasClass("appns.namespaced.Class")
			.and(brbootstrap).containsFileWithContents("library.manifest", "js:")
			.and(brbootstrap).containsFile("bootstrap.js")
			.and(appLib).containsFileWithContents("library.manifest", "js:")
			.and(brbootstrap).containsFile("appLib.js")
			.and(aspect).indexPageHasContent("<@new-js.bundle@/>\n"+
					"appns.namespaced.Class\n"+
					"require('appLib');\n"+
					"require('appns.node.Class');\n" );
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsOrderedTextFragments(
				"thirdparty/appLib/bundle.js", 
				"thirdparty/br-bootstrap/bundle.js", 
				"namespaced-js/package-definitions.js", 
				"namespaced-js/module/appns/namespaced/Class.js", 
				"node-js/module/appns/node/Class.js" );
	}
	
}
