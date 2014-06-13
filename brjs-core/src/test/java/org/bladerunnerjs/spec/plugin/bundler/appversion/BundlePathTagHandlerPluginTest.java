package org.bladerunnerjs.spec.plugin.bundler.appversion;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BundlePathTagHandlerPluginTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private StringBuffer response;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			
			response = new StringBuffer();
	}
	
	@Test
	public void appVersionDevContentIsIncluded() throws Exception {
		given(brjs).hasDevVersion("dev")
			.and(aspect).indexPageHasContent("<@bundle.path@/>");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsText( "../v/dev" );
	}
	
	@Test
	public void appVersionProdContentIsIncluded() throws Exception {
		given(brjs).hasProdVersion("1234")
			.and(aspect).indexPageHasContent("<@bundle.path@/>");
		when(aspect).indexPageLoadedInProd(response, "en_GB");
		then(response).containsText( "../v/1234" );
	}
	
}
