package org.bladerunnerjs.spec.plugin.bundler.composite;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.BladeWorkbench;
import org.junit.Before;
import org.junit.Test;


public class CompositeJsTagHandlerPluginTest extends SpecTest
{
	private App app;
	private Aspect aspect;
	private JsLib appLib, brbootstrap, thirdpartyLib;
	private Bladeset bladeset;
	private Blade blade;
	private BladeWorkbench workbench;
	private StringBuffer pageResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			appLib = app.jsLib("appLib");
			bladeset = app.bladeset("bs");
			bladeset.blade("b1");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
			brbootstrap = brjs.sdkLib("br-bootstrap");
			thirdpartyLib = brjs.sdkLib("thirdpartyLib");
	}
	
	@Test
	public void inDevSeparateJsFileRequestsAreGeneratedByDefault() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).indexPageHasContent("<@js.bundle@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("v/dev/aliasing/bundle.js", "v/dev/app-meta/version.js", "v/dev/common-js/module/appns/Class1.js");
	}
	
	@Test
	public void inProdASingleBundlerRequestIsGeneratedByDefault() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).indexPageHasContent("<@js.bundle@/>");
		when(aspect).indexPageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsText("/js/prod/combined/bundle.js");
	}
	
	@Test
	public void noRequestPathsAreGeneratedInDevIfThereAreNoClasses() throws Exception {
		given(aspect).indexPageHasContent("<@js.bundle@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("v/dev/aliasing/bundle.js", "v/dev/app-meta/version.js");
	}
	
	@Test
	public void devMinifierAttributeCanAllowJsFilesToBeCombinedEvenInDev() throws Exception {
		given(aspect).indexPageHasContent("<@js.bundle dev-minifier='combined'@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("v/dev/js/dev/combined/bundle.js");
	}
		
	@Test
	public void prodMinifierAttributeCanAllowJsFilesToBeServedAsSeparateFiles() throws Exception {		
		given(aspect).hasClass("appns/Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).indexPageHasContent("<@js.bundle prod-minifier='none'@/>");
		when(aspect).indexPageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsRequests("v/prod/thirdparty/bundle.js", "v/prod/aliasing/bundle.js", "v/prod/app-meta/version.js", "v/prod/common-js/bundle.js");
	}
	
	@Test
	public void theAliasBlobRequestIsOutputAfterThirdpartyLibrariesButBeforeTheClasses() throws Exception {
		given(aspect).hasCommonJsPackageStyle("src/appns/node")
		.and(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
		.and(aspect).hasClass("appns/node/Class")
		.and(aspect).hasClass("appns.namespaced.Class")
		.and(brbootstrap).containsFileWithContents("thirdparty-lib.manifest", "exports: brbootstrap")
		.and(brbootstrap).containsFile("bootstrap.js")
		.and(appLib).containsFileWithContents("thirdparty-lib.manifest", "exports: applib")
		.and(brbootstrap).containsFile("appLib.js")
		.and(aspect).indexPageHasContent("<@js.bundle@/>\n"+
				"appns.namespaced.Class\n"+
				"require('appLib');\n"+
				"require('appns.node.Class');\n" );
	when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
	then(pageResponse).containsOrderedTextFragments(
			"thirdparty/br-bootstrap/bundle.js", 
			"thirdparty/appLib/bundle.js",
			"aliasing/bundle.js",
			"common-js/module/appns/node/Class.js",
			"namespaced-js/package-definitions.js", 
			"namespaced-js/module/appns/namespaced/Class.js"); 
	}
	
	@Test
	public void seperateScriptTagsAreGeneratedInTheCorrectOrder() throws Exception {
		given(aspect).hasCommonJsPackageStyle("src/appns/node")
			.and(aspect).hasNamespacedJsPackageStyle("src/appns/namespaced")
			.and(aspect).hasClass("appns/node/Class")
			.and(aspect).hasClass("appns.namespaced.Class")
			.and(brbootstrap).containsFileWithContents("thirdparty-lib.manifest", "exports: brbootstrap")
			.and(brbootstrap).containsFile("bootstrap.js")
			.and(appLib).containsFileWithContents("thirdparty-lib.manifest", "exports: applib")
			.and(brbootstrap).containsFile("appLib.js")
			.and(aspect).indexPageHasContent("<@js.bundle@/>\n"+
					"appns.namespaced.Class\n"+
					"require('appLib');\n"+
					"require('appns.node.Class');\n" );
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsOrderedTextFragments(
				"thirdparty/br-bootstrap/bundle.js", 
				"thirdparty/appLib/bundle.js", 
				"common-js/module/appns/node/Class.js",
				"namespaced-js/package-definitions.js", 
				"namespaced-js/module/appns/namespaced/Class.js" ); 
	}
	
	// Workbench
	@Test
	public void seperateScriptTagsAreGeneratedInTheCorrectOrderForWorkbenches() throws Exception {
		given(exceptions).arentCaught();
		
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1")
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: file1.js \n"+"exports: thirdpartylib")
			.and(thirdpartyLib).containsFile("file1.js")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).classDependsOnThirdpartyLib("appns.bs.b1.Class1", thirdpartyLib)
			.and(workbench).indexPageHasContent("<@js.bundle@/>\n"+
					"appns.bs.b1.Class1\n"+
					"appns.Class1\n");
		when(workbench).pageLoaded(pageResponse, "en_GB");
		then(pageResponse).containsOrderedTextFragments(
				"<script type='text/javascript' src='v/dev/thirdparty/thirdpartyLib/bundle.js'></script>", 
				"<script type='text/javascript' src='v/dev/aliasing/bundle.js'></script>",
				"<script type='text/javascript' src='v/dev/namespaced-js/package-definitions.js'></script>",
				"<script type='text/javascript' src='v/dev/namespaced-js/module/appns/bs/b1/Class1.js'></script>",
				"appns.bs.b1.Class1",
				"appns.Class1");
	}
	
}
