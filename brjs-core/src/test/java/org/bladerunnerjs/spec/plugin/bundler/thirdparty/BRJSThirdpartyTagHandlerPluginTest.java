package org.bladerunnerjs.spec.plugin.bundler.thirdparty;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class BRJSThirdpartyTagHandlerPluginTest extends SpecTest {
	
	private App app;
	private Aspect aspect;
	private JsLib thirdpartyLib;
	
	private StringBuffer pageResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			thirdpartyLib = app.nonBladeRunnerLib("thirdparty-lib");
	}	
	
	@Test
	public void inDevSeparateJsFileRequestsAreGeneratedByDefault() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "exports: thirdpartylib")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("thirdparty/thirdparty-lib/bundle.js")
			.and(pageResponse).doesNotContainText("<@thirdparty.bundle@/>");
	}
	
	@Test
	public void inProdASingleBundlerRequestIsGeneratedByDefault() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "exports: thirdpartylib")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(aspect).indexPageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsRequests("thirdparty/bundle.js")
			.and(pageResponse).doesNotContainText("<@thirdparty.bundle@/>");
	}
	
	@Test
	public void noRequestPathsAreGeneratedInDevIfThereAreNoLibraries() throws Exception {
		given(aspect).indexPageHasContent("<@thirdparty.bundle@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests()
			.and(pageResponse).doesNotContainText("<@thirdparty.bundle@/>");
	}
	
}
