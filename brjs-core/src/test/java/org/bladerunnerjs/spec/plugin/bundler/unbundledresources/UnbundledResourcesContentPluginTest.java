package org.bladerunnerjs.spec.plugin.bundler.unbundledresources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class UnbundledResourcesContentPluginTest extends SpecTest {
	private StringBuffer response = new StringBuffer();
	private List<String> requestsList;
	private ContentPlugin unbundledResourcesPlugin;
	private App app;
	private Aspect appAspect;
	private App sysapp;
	private Aspect sysappAspect;
	private File unbundledResources;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appAspect = app.aspect("default");
			unbundledResources = appAspect.file("unbundled-resources");
			sysapp = brjs.systemApp("sysapp");
			sysappAspect = sysapp.aspect("default");
			
		unbundledResourcesPlugin = brjs.plugins().contentPlugin("unbundled-resources");
		requestsList = new ArrayList<String>();
	}
	
	@Test
	public void ifThereAreNoFilesInUnbundledResourcesThenNoRequestsWillBeGenerated() throws Exception {
		given(appAspect).indexPageHasContent("index page");
		then(appAspect).prodAndDevRequestsForContentPluginsAre("unbundled-resources");
	}
	
	@Test
	public void ifThereAreFilesInUnbundledResourcesThenRequestsWillBeGenerated() throws Exception {
		given(appAspect).indexPageHasContent("index page")
			.and(unbundledResources).containsFile("some-file")
			.and(unbundledResources).containsFile("some-dir/some-file");
		then(appAspect).prodAndDevRequestsForContentPluginsAre("unbundled-resources", "unbundled-resources/some-file", "unbundled-resources/some-dir/some-file");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appAspect).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceived("unbundled-resources/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInASystemAppUnbundledResources() throws Exception
	{
		given(sysapp).hasBeenCreated()
			.and(sysappAspect).hasBeenCreated()
			.and(sysappAspect).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(sysappAspect).requestReceived("unbundled-resources/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInASubDirectoryInUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appAspect).containsFileWithContents("unbundled-resources/a/dir/someFile.txt", "some file contents");
		when(appAspect).requestReceived("unbundled-resources/a/dir/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void exceptionIsThrownIfTheFileDoesntExists() throws Exception
	{
		given(app).hasBeenCreated();
		when(appAspect).requestReceived("unbundled-resources/someFile.txt", response);
		then(exceptions).verifyException(ContentProcessingException.class, "app1/default-aspect/unbundled-resources/someFile.txt");
	}
	
	@Test
	public void unbundledResourcesHasCorrectPossibleDevPaths() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(appAspect).containsFiles("unbundled-resources/someFile.txt", "unbundled-resources/a/dir/someFile.txt");
    	when(unbundledResourcesPlugin).getPossibleDevRequests(appAspect, requestsList);
		thenRequests(requestsList).entriesEqual(
    			"unbundled-resources/someFile.txt",
    			"unbundled-resources/a/dir/someFile.txt"
    	);
	}
	
	@Test
	public void unbundledResourcesHasCorrectPossibleProdPaths() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appAspect).containsFiles("unbundled-resources/someFile.txt", "unbundled-resources/a/dir/someFile.txt");
		when(unbundledResourcesPlugin).getPossibleProdRequests(appAspect, requestsList);
		thenRequests(requestsList).entriesEqual(
				"unbundled-resources/someFile.txt",
				"unbundled-resources/a/dir/someFile.txt"
		);
	}
	
	@Test
	public void jspsCanBeUsedInUnbundledResources() throws Exception
	{
		try {
    		given(app).hasBeenPopulated()
        		.and(appAspect).containsFileWithContents("unbundled-resources/file.jsp", "2 + 2 = <%= 2 + 2 %>")
        		.and(brjs).hasDevVersion("1234")
        		.and(brjs.applicationServer(appServerPort)).started();
        	then(brjs.applicationServer(appServerPort)).requestForUrlReturns("/app1/v/123/unbundled-resources/file.jsp", "2 + 2 = 4");
		} finally {
			brjs.applicationServer().stop();
		}
	}
	
	@Test //TODO: this test is a potentially a false positive - we don't have any way of testing binary data in the spec test framework
	public void binaryDataCanBeUsedFromUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appAspect).containsBinaryFileWithContents("unbundled-resources/binary.data", (""+Math.PI).getBytes());
		when(appAspect).requestReceived("unbundled-resources/binary.data", response);
		then(response).textEquals(""+Math.PI);
	}
	
}
