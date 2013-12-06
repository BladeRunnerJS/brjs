package org.bladerunnerjs.spec.plugin.bundler;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
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
	
	//TODO: change the new-js.bundle back to js.bundle once the legacy js bundle tag handler is deleted
	
	@Test
	public void inDevSeparateJsFileRequestsAreGeneratedByDefault() throws Exception {
		given(aspect).hasClass("mypkg.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).indexPageHasContent("<@new-js.bundle@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("node-js/module/mypkg/Class1.js", "namespaced-js/package-definitions.js");
	}
	
	@Test
	public void inProdASingleBundlerRequestIsGeneratedByDefault() throws Exception {
		given(aspect).hasClass("mypkg.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
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
		given(aspect).hasClass("mypkg.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "mypkg.Class1")
			.and(aspect).indexPageHasContent("<@new-js.bundle prod-minifier='none'@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("node-js/module/mypkg/Class1.js", "namespaced-js/package-definitions.js");
	}
}
