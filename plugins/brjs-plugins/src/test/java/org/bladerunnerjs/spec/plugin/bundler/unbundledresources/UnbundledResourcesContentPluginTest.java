package org.bladerunnerjs.spec.plugin.bundler.unbundledresources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.Before;
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
	private MemoizedFile unbundledResources;
	private Aspect defaultAspect;
	private Bladeset defaultBladeset;
	private Blade bladeInDefaultBladeset;
	private File bladesetUnbundledResources;
	private File bladeUnbundledResources;
	private File bladeWorkbenchUnbundledResources;
	private File bladesetWorkbenchUnbundledResources;
	private Bladeset bladeset;	
	private Blade blade;
	private Workbench<Blade> bladeWorkbench;
	private Workbench<Bladeset> bladesetWorkbench;
	private TemplateGroup templates;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appAspect = app.aspect("default");
			defaultAspect = app.defaultAspect();
			defaultBladeset = app.defaultBladeset();
			bladeInDefaultBladeset = defaultBladeset.blade("b2");
			unbundledResources = appAspect.file("unbundled-resources");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeWorkbench = blade.workbench();
			bladesetWorkbench = bladeset.workbench();
			bladesetUnbundledResources = bladeset.file("unbundled-resources");
			bladeUnbundledResources = blade.file("unbundled-resources");
			bladeWorkbenchUnbundledResources = bladeWorkbench.file("unbundled-resources");
			bladesetWorkbenchUnbundledResources = bladesetWorkbench.file("unbundled-resources");
			sysapp = brjs.systemApp("sysapp");
			sysappAspect = sysapp.aspect("default");
			templates = brjs.sdkTemplateGroup("default");
			
		binaryResponseFile = FileUtils.createTemporaryFile( this.getClass() );
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
			.and(bladeset).hasBeenCreated()
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
			.and(bladeset).hasBeenCreated()
			.and(bladeset).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("unbundled-resources/bladeset_bs/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInBladesetUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
			.and(bladeset).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("/unbundled-resources/bladeset_bs/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void ifThereAreFilesInBladeUnbundledResourcesThenRequestsWillBeGenerated() throws Exception {
		given(appAspect).indexPageHasContent("index page")
			.and(blade).hasBeenCreated()
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
			.and(blade).hasBeenCreated()
			.and(blade).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("unbundled-resources/bladeset_bs/blade_b1/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInBladeUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(blade).hasBeenCreated()
			.and(blade).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("/unbundled-resources/bladeset_bs/blade_b1/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void ifThereAreFilesInBladeWorkbenchUnbundledResourcesThenRequestsWillBeGenerated() throws Exception {
		given(appAspect).indexPageHasContent("index page")
			.and(blade).hasBeenCreated()
			.and(bladeWorkbenchUnbundledResources).containsFiles("some-file", "some-dir/some-file");
		then(bladeWorkbench).prodAndDevRequestsForContentPluginsAre("unbundled-resources",
				"/unbundled-resources/bladeset_bs/blade_b1/workbench/some-file", 
				"unbundled-resources/bladeset_bs/blade_b1/workbench/some-file", 
				"/unbundled-resources/bladeset_bs/blade_b1/workbench/some-dir/some-file",
				"unbundled-resources/bladeset_bs/blade_b1/workbench/some-dir/some-file");
	}
	
	@Test
	public void ifThereAreFilesInBladesetWorkbenchUnbundledResourcesThenRequestsWillBeGenerated() throws Exception {
		given(appAspect).indexPageHasContent("index page")
			.and(templates).templateGroupCreated()
			.and(bladeset).hasBeenPopulated()
			.and(bladesetWorkbenchUnbundledResources).containsFiles("some-file", "some-dir/some-file");
		then(bladesetWorkbench).prodAndDevRequestsForContentPluginsAre("unbundled-resources",
				"/unbundled-resources/bladeset_bs/workbench/some-file", 
				"unbundled-resources/bladeset_bs/workbench/some-file", 
				"/unbundled-resources/bladeset_bs/workbench/some-dir/some-file",
				"unbundled-resources/bladeset_bs/workbench/some-dir/some-file");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInBladeWorkbenchVersionedUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(blade).hasBeenCreated()
			.and(bladeWorkbench).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("unbundled-resources/bladeset_bs/blade_b1/workbench/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInBladesetWorkbenchVersionedUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(templates).templateGroupCreated()
			.and(bladeset).hasBeenPopulated()
			.and(bladesetWorkbench).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("unbundled-resources/bladeset_bs/workbench/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInBladeWorkbenchUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(blade).hasBeenCreated()
			.and(bladeWorkbench).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("/unbundled-resources/bladeset_bs/blade_b1/workbench/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInBladesetWorkbenchUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(templates).templateGroupCreated()
			.and(bladeset).hasBeenPopulated()
			.and(bladesetWorkbench).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(appAspect).requestReceivedInDev("/unbundled-resources/bladeset_bs/workbench/someFile.txt", response);
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
    		given(app).hasBeenCreated()
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
	
	@Test
	public void unbundledResourcesCanBeUsedFromDefaultBladesets() throws Exception {
		given(defaultAspect).hasBeenCreated()
			.and(defaultBladeset).hasBeenCreated()
    		.and(defaultBladeset).containsFileWithContents("unbundled-resources/someFile.txt", "default bladeset unbundled-resources file");
    	when(unbundledResourcesPlugin).getPossibleProdRequests(defaultAspect, requestsList)
    		.and(defaultAspect).requestReceivedInDev("unbundled-resources/someFile.txt", response);
    	thenRequests(requestsList).entriesEqual(
    			"/unbundled-resources/someFile.txt",
    			"unbundled-resources/someFile.txt")
    		.and(response).textEquals("default bladeset unbundled-resources file");
	}
	
	@Test
	public void unbundledResourcesCanBeUsedFromABladeWithinDefaultBladesets() throws Exception {
		given(defaultAspect).hasBeenCreated()
			.and(defaultBladeset).hasBeenCreated()
			.and(bladeInDefaultBladeset).hasBeenCreated()
    		.and(bladeInDefaultBladeset).containsFileWithContents("unbundled-resources/someFile.txt", "blade in default bladeset unbundled-resources file");
    	when(unbundledResourcesPlugin).getPossibleProdRequests(defaultAspect, requestsList)
    		.and(defaultAspect).requestReceivedInDev("unbundled-resources/bladeset_default/blade_b2/someFile.txt", response);
    	thenRequests(requestsList).entriesEqual(
    			"/unbundled-resources/bladeset_default/blade_b2/someFile.txt",
    			"unbundled-resources/bladeset_default/blade_b2/someFile.txt")
    		.and(response).textEquals("blade in default bladeset unbundled-resources file");
	}
}
