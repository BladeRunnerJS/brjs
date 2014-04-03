package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class InvalidAssetPluginHandling extends SpecTest {
	private App app;
	private Aspect aspect;
	private InvalidAssetPlugin invalidAssetPlugin = new InvalidAssetPlugin();
	private StringBuffer pageResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasAssetPlugins(invalidAssetPlugin)
			.and(brjs).automaticallyFindsContentPlugins()
			.and(brjs).automaticallyFindsAssetLocationProducers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test
	public void thirdPartyAssetLocationThrowsAnExceptionIfObtainAssetIsUsedForInvalidLocation() throws Exception
	{
		invalidAssetPlugin.enable();
		
		given(app).hasBeenCreated()
			.and(aspect).hasClass("appns/Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en/combined/bundle.js", pageResponse);
		then(exceptions).verifyException(AssetFileInstantationException.class);
	}
}
