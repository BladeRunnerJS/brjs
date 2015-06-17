package org.bladerunnerjs.spec.plugin.bundler.favicon;

import java.io.File;
import java.io.FileOutputStream;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FavIconContentPluginTest extends SpecTest {

	private App app;
	private Aspect appAspect;
	private File binaryResponseFile;
	private FileOutputStream binaryResponse;
	private ApplicationServer appServer;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		app = brjs.app("app1");
		appAspect = app.aspect("default");
		appServer = brjs.applicationServer(appServerPort);
		binaryResponseFile = FileUtils.createTemporaryFile( this.getClass() );
		binaryResponse = new FileOutputStream(binaryResponseFile);
		
		brjs.appJars().create();
	}
	
	@After
	public void stopServer() throws Exception
	{
		given(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs.applicationServer(appServerPort)).requestTimesOutFor("/");
	}
		
	@Test
	public void ifThereIsNoFavIconThenNoRequestsWillBeGenerated() throws Exception {
		given(appAspect).indexPageHasContent("index page");
		then(appAspect).prodAndDevRequestsForContentPluginsAreEmpty("favicon.ico");
	}

	@Test
	public void theContentOfTheFavIconIsTheSameAsTheResponseObtainedForTheRequest() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appAspect).containsFile("favicon.ico");
		when(appAspect).requestReceivedInDev("favicon.ico", binaryResponse);
    	then(binaryResponseFile).sameAsFile(appAspect.file("favicon.ico").getAbsolutePath());
	}
	
	@Test
	public void exceptionIsThrownIfFavIconDoesNotExist() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appAspect).doesNotContainFile("favicon.ico");
		when(appAspect).requestReceivedInDev("favicon.ico", binaryResponse);
    	then(exceptions).verifyException(ContentProcessingException.class, "favicon.ico");
	}

	@Test
	public void theRequestForAnExistingFavIconReturnsCode200() throws Exception {
		given(app).hasBeenCreated()
			.and(appAspect).containsFile("favicon.ico");
		when(appServer).started();
		then(appServer).requestForUrlHasResponseCode("/app1/favicon.ico", 200);
	}
	
	@Test
	public void theRequestForANonExistingFavIconReturnsCode404() throws Exception {
		given(app).hasBeenCreated()
			.and(appAspect).doesNotContainFile("favicon.ico");
		when(appServer).started();
		then(appServer).requestForUrlHasResponseCode("/app1/favicon.ico", 404);
	}
	
	@Test
	public void theContentTypeOfTheResponseObtainedForTheRequestIsImageXIcon() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appAspect).containsFileCopiedFrom("favicon.ico", "src/test/resources/favicon.ico");
		when(appServer).started();
		then(appServer).contentTypeForRequestIs("/app1/favicon.ico", "image/x-icon");
	}
}
