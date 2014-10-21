package org.bladerunnerjs.spec.plugin.bundler.unbundledresources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class UnbundledResourcesContentPluginTest extends SpecTest {
	private StringBuffer response = new StringBuffer();
	private OutputStream binaryResponse;
	private File binaryResponseFile;
	private List<String> requestsList;
	private ContentPlugin unbundledResourcesPlugin;
	private App app;
	private Aspect appAspect;
	private App sysapp;
	private Aspect sysappAspect;
	private File unbundledResources;
	private Aspect defaultAspect;
	private File bladesetUnbundledResources;
	private File bladeUnbundledResources;
	private File workbenchUnbundledResources;
	private Bladeset bladeset;	
	private Blade blade;
	private Workbench workbench;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appAspect = app.aspect("default");
			defaultAspect = app.defaultAspect();
			unbundledResources = appAspect.file("unbundled-resources");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
			bladesetUnbundledResources = bladeset.file("unbundled-resources");
			bladeUnbundledResources = blade.file("unbundled-resources");
			sysapp = brjs.systemApp("sysapp");
			sysappAspect = sysapp.aspect("default");
			
		binaryResponseFile = FileUtility.createTemporaryFile( this.getClass() );
		binaryResponse = new FileOutputStream(binaryResponseFile);
		unbundledResourcesPlugin = brjs.plugins().contentPlugin("unbundled-resources");
		requestsList = new ArrayList<String>();
		
		brjs.appJars().create();
	}
	
	@Test
	public void ifThereAreNoFilesInUnbundledResourcesThenNoRequestsWillBeGenerated() throws Exception {
		given(appAspect).indexPageHasContent("index page");
		then(appAspect).prodAndDevRequestsForContentPluginsAreEmpty("unbundled-resources");
	}
	
	@Test
	public void ifThereAreFilesInUnbundledResourcesThenRequestsWillBeGenerated() throws Exception {
		given(appAspect).indexPageHasContent("index page")
			.and(unbundledResources).containsFile("some-file")
			.and(unbundledResources).containsFile("some-dir/some-file");
		then(appAspect).prodAndDevRequestsForContentPluginsAre( "unbundled-resources",
				"/unbundled-resources/some-file", 
				"unbundled-resources/some-file", 
				"/unbundled-resources/some-dir/some-file",
				"unbundled-resources/some-dir/some-file");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appAspect).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("unbundled-resources/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void ifThereAreFilesInBladesetUnbundledResourcesThenRequestsWillBeGenerated() throws Exception {
		given(appAspect).indexPageHasContent("index page")
			.and(bladeset).hasBeenPopulated()
			.and(bladesetUnbundledResources).containsFile("some-file")
			.and(bladesetUnbundledResources).containsFile("some-dir/some-file");
		then(appAspect).prodAndDevRequestsForContentPluginsAre( "unbundled-resources",
				"/unbundled-resources/bladeset_bs/some-file", 
				"unbundled-resources/bladeset_bs/some-file", 
				"/unbundled-resources/bladeset_bs/some-dir/some-file",
				"unbundled-resources/bladeset_bs/some-dir/some-file");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInBladesetVersionedUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenPopulated()
			.and(bladeset).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("unbundled-resources/bladeset_bs/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInBladesetUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenPopulated()
			.and(bladeset).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("/unbundled-resources/bladeset_bs/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void ifThereAreFilesInBladeUnbundledResourcesThenRequestsWillBeGenerated() throws Exception {
		given(appAspect).indexPageHasContent("index page")
			.and(bladeset).hasBeenPopulated()
			.and(blade).hasBeenPopulated()
			.and(bladeUnbundledResources).containsFile("some-file")
			.and(bladeUnbundledResources).containsFile("some-dir/some-file");
		then(appAspect).prodAndDevRequestsForContentPluginsAre( "unbundled-resources",
				"/unbundled-resources/bladeset_bs/blade_b1/some-file", 
				"unbundled-resources/bladeset_bs/blade_b1/some-file", 
				"/unbundled-resources/bladeset_bs/blade_b1/some-dir/some-file",
				"unbundled-resources/bladeset_bs/blade_b1/some-dir/some-file");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInBladeVersionedUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenPopulated()
			.and(blade).hasBeenPopulated()
			.and(blade).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("unbundled-resources/bladeset_bs/blade_b1/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInBladeUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenPopulated()
			.and(blade).hasBeenPopulated()
			.and(blade).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("/unbundled-resources/bladeset_bs/blade_b1/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Ignore
	@Test
	public void ifThereAreFilesInWorkbenchUnbundledResourcesThenRequestsWillBeGenerated() throws Exception {
		given(appAspect).indexPageHasContent("index page")
			.and(bladeset).hasBeenPopulated()
			.and(blade).hasBeenPopulated()
			.and(workbench).hasBeenPopulated()
			.and(workbenchUnbundledResources).containsFile("some-file")
			.and(workbenchUnbundledResources).containsFile("some-dir/some-file");
		then(appAspect).prodAndDevRequestsForContentPluginsAre( "unbundled-resources",
				"/unbundled-resources/bladeset_bs/blade_b1/workbench/some-file", 
				"unbundled-resources/bladeset_bs/blade_b1/workbench/some-file", 
				"/unbundled-resources/bladeset_bs/blade_b1/workbench/some-dir/some-file",
				"unbundled-resources/bladeset_bs/blade_b1/workbench/some-dir/some-file");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInWorkbenchVersionedUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenPopulated()
			.and(blade).hasBeenPopulated()
			.and(workbench).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("unbundled-resources/bladeset_bs/blade_b1/workbench/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInWorkbenchUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenPopulated()
			.and(blade).hasBeenPopulated()
			.and(workbench).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("/unbundled-resources/bladeset_bs/blade_b1/workbench/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void unversionedRequestsCanBeMadeForAFileInUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appAspect).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("/unbundled-resources/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInASystemAppUnbundledResources() throws Exception
	{
		given(sysapp).hasBeenCreated()
			.and(sysappAspect).hasBeenCreated()
			.and(sysappAspect).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(sysappAspect).requestReceivedInDev("unbundled-resources/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInASubDirectoryInUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appAspect).containsFileWithContents("unbundled-resources/a/dir/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("unbundled-resources/a/dir/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void exceptionIsThrownIfTheFileDoesntExists() throws Exception
	{
		given(app).hasBeenCreated();
		when(appAspect).requestReceivedInDev("unbundled-resources/someFile.txt", response);
		then(exceptions).verifyException(ContentProcessingException.class, "app1/default-aspect/unbundled-resources/someFile.txt");
	}
	
	@Test
	public void unbundledResourcesHasCorrectPossibleDevPaths() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(appAspect).containsFiles("unbundled-resources/someFile.txt", "unbundled-resources/a/dir/someFile.txt");
    	when(unbundledResourcesPlugin).getPossibleDevRequests(appAspect, requestsList);
		thenRequests(requestsList).entriesEqual(
    			"/unbundled-resources/someFile.txt",
    			"unbundled-resources/someFile.txt",
    			"/unbundled-resources/a/dir/someFile.txt",
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
				"/unbundled-resources/someFile.txt",
				"unbundled-resources/someFile.txt",
				"/unbundled-resources/a/dir/someFile.txt",
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
			given(brjs.applicationServer(appServerPort)).stopped()
				.and(brjs.applicationServer(appServerPort)).requestTimesOutFor("/");
		}
	}
	
	@Test
	public void imagesArentCorrupt() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(appAspect).hasBeenCreated()
    		.and(appAspect).containsFileCopiedFrom("unbundled-resources/br-logo.png", "src/test/resources/br-logo.png");
    	when(appAspect).requestReceivedInDev("unbundled-resources/br-logo.png", binaryResponse);
    	then(binaryResponseFile).sameAsFile("src/test/resources/br-logo.png");
	}
	
	@Test
	public void unbundledResourcesCanBeUsedFromDefaultAspects() throws Exception {
		given(defaultAspect).hasBeenCreated()
    		.and(defaultAspect).containsFileWithContents("unbundled-resources/someFile.txt", "default aspect unbundled-resources file");
    	when(unbundledResourcesPlugin).getPossibleProdRequests(defaultAspect, requestsList)
    		.and(defaultAspect).requestReceivedInDev("unbundled-resources/someFile.txt", response);
    	thenRequests(requestsList).entriesEqual(
    			"/unbundled-resources/someFile.txt",
    			"unbundled-resources/someFile.txt")
    		.and(response).textEquals("default aspect unbundled-resources file");
	}
	
}
