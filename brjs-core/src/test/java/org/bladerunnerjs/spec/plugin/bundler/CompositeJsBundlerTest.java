package org.bladerunnerjs.spec.plugin.bundler;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CompositeJsBundlerTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer page = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Ignore
	@Test
	public void inDevSeparateJsFilesRequestedAreGeneratedByDefeault() {
		given(aspect).indexPageHasContent("<@js.bundle@/>");
		when(aspect).pageLoadedInDev(page, "en_GB");
		then(page).containsRequests(page, "/app1/default-aspect/src/app.js");
	}
	
	@Ignore
	@Test
	public void devMinifierTagCanAllowJsFilesToBeCombined() {
		given(aspect).indexPageHasContent("<@js.bundle dev-minifier='combined'@/>");
		when(aspect).pageLoadedInDev(page, "en_GB");
		then(page).containsRequests(page, "/js/js.bundle");
	}
	
	@Ignore
	@Test
	public void inProdASingleBundlerRequestIsGeneratedByDefault() {
		given(aspect).indexPageHasContent("<@js.bundle@/>");
		when(aspect).pageLoadedInProd(page, "en_GB");
		then(page).containsRequests(page, "/js/js.bundle");
	}
	
	@Ignore
	@Test
	public void prodMinifierTagCanAllowJsFilesToBeServedAsSeparateFiles() {
		given(aspect).indexPageHasContent("<@js.bundle@ prod-minifier='none'/>");
		when(aspect).pageLoadedInProd(page, "en_GB");
		then(page).containsRequests(page, "/app1/default-aspect/src/app.js");
	}
	
}
