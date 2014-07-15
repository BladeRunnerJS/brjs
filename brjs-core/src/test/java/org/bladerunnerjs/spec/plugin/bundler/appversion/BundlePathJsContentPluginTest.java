package org.bladerunnerjs.spec.plugin.bundler.appversion;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.utility.AppMetadataUtility;
import org.junit.Before;
import org.junit.Test;

public class BundlePathJsContentPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer requestResponse = new StringBuffer();
	private SdkJsLib bootstrapLib;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bootstrapLib = brjs.sdkLib("br-bootstrap");
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
