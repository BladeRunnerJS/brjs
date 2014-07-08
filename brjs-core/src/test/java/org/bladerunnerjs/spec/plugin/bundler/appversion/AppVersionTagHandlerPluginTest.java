package org.bladerunnerjs.spec.plugin.bundler.appversion;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class AppVersionTagHandlerPluginTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private StringBuffer response;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			
			response = new StringBuffer();
	}
	
	@Test
	public void appVersionDevContentIsIncluded() throws Exception {
		given(brjs).hasDevVersion("dev")
			.and(aspect).indexPageHasContent("<@app.version@/>");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsText( "dev" );
	}
	
	@Test
	public void appVersionProdContentIsIncluded() throws Exception {
		given(brjs).hasProdVersion("1234")
			.and(aspect).indexPageHasContent("<@app.version@/>");
		when(aspect).indexPageLoadedInProd(response, "en_GB");
		then(response).containsText( "1234" );
	}
}
