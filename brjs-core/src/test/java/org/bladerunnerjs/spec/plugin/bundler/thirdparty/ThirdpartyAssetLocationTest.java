package org.bladerunnerjs.spec.plugin.bundler.thirdparty;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class ThirdpartyAssetLocationTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib thirdpartyLib;
	private InvalidLocationJsAssetPlugin invalidLocationJsAssetPlugin = new InvalidLocationJsAssetPlugin();
	private NonExistentAssetJsAssetPlugin nonExistentAssetJsAssetPlugin = new NonExistentAssetJsAssetPlugin();
	private StringBuffer pageResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasAssetPlugins(invalidLocationJsAssetPlugin, nonExistentAssetJsAssetPlugin)
			.and(brjs).automaticallyFindsContentPlugins()
			.and(brjs).automaticallyFindsAssetLocationProducers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			thirdpartyLib = app.nonBladeRunnerLib("thirdparty-lib");
	}
	
	@Test
	public void thirdPartyAssetLocationThrowsAnExceptionIfObtainAssetIsUsedForInvalidLocation() throws Exception
	{
		invalidLocationJsAssetPlugin.enable();
		
		given(app).hasBeenCreated()
			.and(aspect).indexPageRequires(thirdpartyLib)
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "js: libs.js\n"+"exports: thirdpartylib")
			.and(thirdpartyLib).containsFile("lib.js");
		when(app).requestReceived("/default-aspect/js/dev/en/combined/bundle.js", pageResponse);
		then(exceptions).verifyException(AssetFileInstantationException.class, thirdpartyLib.file("invalid-location").getPath(), thirdpartyLib.dir().getPath());
	}
	
	@Test
	public void thirdPartyAssetLocationThrowsAnExceptionIfObtainAssetIsUsedForNonExistentAsset() throws Exception
	{
		nonExistentAssetJsAssetPlugin.enable();
		
		given(app).hasBeenCreated()
			.and(aspect).indexPageRequires(thirdpartyLib)
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "js: libs.js\n"+"exports: thirdpartylib")
			.and(thirdpartyLib).containsFile("lib.js");
		when(app).requestReceived("/default-aspect/js/dev/en/combined/bundle.js", pageResponse);
		then(exceptions).verifyException(AssetFileInstantationException.class, "non-existent-asset");
	}
}
