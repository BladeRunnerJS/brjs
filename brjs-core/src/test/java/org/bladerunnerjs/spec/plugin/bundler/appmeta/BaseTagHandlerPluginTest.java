package org.bladerunnerjs.spec.plugin.bundler.appmeta;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class BaseTagHandlerPluginTest extends SpecTest {
	
	private App app;
	private Aspect aspect;
	private StringBuffer requestResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test
	public void baseTagWorksInDev() throws Exception {
		given(brjs).hasDevVersion("dev")
			.and(aspect).indexPageHasContent("<@base.tag@/>");
		when(aspect).indexPageLoadedInDev(requestResponse, "en_GB");
		then(requestResponse).containsTextOnce( "<base href=\"../\"/>" );
	}
	
	@Test
	public void baseTagWorksInProd() throws Exception {
		given(brjs).hasProdVersion("1234")
			.and(aspect).indexPageHasContent("<@base.tag@/>");
		when(aspect).indexPageLoadedInProd(requestResponse, "en_GB");
		then(requestResponse).containsTextOnce( "<base href=\"../\"/>" );
	}
	
}
