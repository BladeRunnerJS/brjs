package org.bladerunnerjs.spec.plugin.bundler.thirdparty;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class ThirdpartyResourceContentPluginTest extends SpecTest {
	
	private App app;
	private Aspect aspect;
	private JsLib thirdpartyLib;
	private StringBuffer pageResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			thirdpartyLib = app.jsLib("thirdparty-lib");
	}	
	
	@Test
	public void assetsInALibCanBeRequestedIndividuallyInDev() throws Exception {
		given(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: lib.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFileWithContents("/some/lib/dirs/some-file.ext", "some file contents")
			.and(aspect).indexPageHasContent("require('"+thirdpartyLib.getName()+"')");
		when(aspect).requestReceived("thirdparty-resource/thirdparty-lib/some/lib/dirs/some-file.ext", pageResponse);
		then(pageResponse).textEquals("some file contents")
			.and(aspect).devRequestsForContentPluginsAre("thirdparty-resource", "thirdparty-resource/thirdparty-lib/thirdparty-lib.manifest",
					"thirdparty-resource/thirdparty-lib/some/lib/dirs/some-file.ext");
	}
	
	@Test
	public void assetsInALibCanBeRequestedIndividuallyInProd() throws Exception {
		given(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: lib.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFileWithContents("/some/lib/dirs/some-file.ext", "some file contents")
			.and(aspect).indexPageHasContent("require('"+thirdpartyLib.getName()+"')");
		when(aspect).requestReceivedInProd("thirdparty-resource/thirdparty-lib/some/lib/dirs/some-file.ext", pageResponse);
		then(pageResponse).textEquals("some file contents")
			.and(aspect).prodRequestsForContentPluginsAre("thirdparty-resource", "thirdparty-resource/thirdparty-lib/thirdparty-lib.manifest",
					"thirdparty-resource/thirdparty-lib/some/lib/dirs/some-file.ext");
	}
	
	@Test
	public void testLibraryResourceForLibraryPresentBothInAppAndSdkIsBundledFromApp() throws Exception
	{
		JsLib appLib = app.jsLib("lib1");
		JsLib sdkLib = brjs.sdkLib("lib1");
		
		given(appLib).hasBeenCreated()
			.and(appLib).containsFileWithContents("thirdparty-lib.manifest", "js: app-lib.js")
			.and(appLib).containsFile("app-lib.js")
			.and(sdkLib).hasBeenCreated()
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "js: sdk-lib.js")
			.and(sdkLib).containsFile("sdk-lib.js");
		when(aspect).requestReceived("thirdparty-resource/lib1/app-lib.js", pageResponse);
		then(pageResponse).textEquals("app-lib.js\n");
	}
	
	@Test
	public void weGetAGoodMessageIfTheLibraryDoesntExist() throws Exception
	{
		given(app).hasBeenCreated();
		when(aspect).requestReceived("thirdparty-resource/libThatDoesntExist/myFile.js", pageResponse);
		then(exceptions).verifyException(ContentProcessingException.class, "libThatDoesntExist");
	}
	
	@Test
	public void weGetAGoodMessageIfTheFileInTheLibraryDoesntExist() throws Exception
	{
		given(app).hasBeenCreated()
			.and(thirdpartyLib).hasBeenCreated();
		when(aspect).requestReceived("thirdparty-resource/thirdparty-lib/myFile.js", pageResponse);
		then(exceptions).verifyException(ContentProcessingException.class, thirdpartyLib.file("myFile.js").getAbsolutePath());
	}
	
	
}
