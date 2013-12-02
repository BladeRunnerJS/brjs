package org.bladerunnerjs.spec.plugin.bundler;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CompositeJsBundlerPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer pageResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).automaticallyFindsTagHandlers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test
	public void inDevSeparateJsFileRequestsAreGeneratedByDefeault() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).indexPageHasContent("<@js.bundle@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("node-js/module/novox/Class1.js");
	}
	
	@Test
	public void inProdASingleBundlerRequestIsGeneratedByDefault() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).indexPageHasContent("<@js.bundle@/>");
		when(aspect).pageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsRequests("js/prod/en_GB/combined/bundle.js");
	}
	
	// TODO: what is this 'package-definitions.js', and is it right that it's being generated even where there are no CaplinJs style classes?
	@Ignore
	@Test
	public void noRequestPathsAreGeneratedInDevIfThereAreNoClasses() throws Exception {
		given(aspect).indexPageHasContent("<@js.bundle@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).isEmpty();
	}
	
	@Test
	public void devMinifierAttributeCanAllowJsFilesToBeCombinedEvenInDev() throws Exception {
		given(aspect).indexPageHasContent("<@js.bundle dev-minifier=\"combined\"@/>"); // TODO: change back to single quotes once TagPluginUtility is updated to use a proper XML parser
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("js/dev/en_GB/combined/bundle.js");
	}
	
	@Test
	public void prodMinifierAttributeCanAllowJsFilesToBeServedAsSeparateFiles() throws Exception {
		given(aspect).hasClass("novox.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "novox.Class1")
			.and(aspect).indexPageHasContent("<@js.bundle prod-minifier=\"none\"@/>"); // TODO: change back to single quotes once TagPluginUtility is updated to use a proper XML parser
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("node-js/module/novox/Class1.js");
	}
}
