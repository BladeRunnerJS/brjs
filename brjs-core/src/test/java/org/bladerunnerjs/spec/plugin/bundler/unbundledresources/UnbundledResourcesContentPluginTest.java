package org.bladerunnerjs.spec.plugin.bundler.unbundledresources;

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
	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private List<String> requestsList;
	private ContentPlugin unbundledResourcesPlugin;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			
		unbundledResourcesPlugin = brjs.plugins().contentProviderForLogicalPath("unbundled-resources");
		requestsList = new ArrayList<String>();
	}
	
	@Test
	public void requestsCanBeMadeForAFileInUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(app).requestReceived("/default-aspect/unbundled-resources/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInASubDirectoryInUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("unbundled-resources/a/dir/someFile.txt", "some file contents");
		when(app).requestReceived("/default-aspect/unbundled-resources/a/dir/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
	@Test
	public void exceptionIsThrownIfTheFileDoesntExists() throws Exception
	{
		given(app).hasBeenCreated();
		when(app).requestReceived("/default-aspect/unbundled-resources/someFile.txt", response);
		then(exceptions).verifyException(ContentProcessingException.class, "app1/unbundled-resources/someFile.txt");
	}
	
	@Test
	public void unbundledResourcesHasCorrectPossibleDevPaths() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(app).containsFiles("unbundled-resources/someFile.txt", "unbundled-resources/a/dir/someFile.txt");
    	when(unbundledResourcesPlugin).getPossibleDevRequests(aspect, requestsList);
		thenRequests(requestsList).entriesEqual(
    			"unbundled-resources/someFile.txt",
    			"unbundled-resources/a/dir/someFile.txt"
    	);
	}
	
}
