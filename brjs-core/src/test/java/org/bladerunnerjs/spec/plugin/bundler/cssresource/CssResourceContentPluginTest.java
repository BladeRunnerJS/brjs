package org.bladerunnerjs.spec.plugin.bundler.cssresource;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CssResourceContentPluginTest extends SpecTest {
	private App app;
	private StringBuffer response = new StringBuffer();
	private JsLib sdkJsLib;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private Workbench workbench;
	private ContentPlugin cssResourcePlugin;
	private List<String> requestsList;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
			sdkJsLib = brjs.sdkLib("sdkLib");
			
		cssResourcePlugin = brjs.plugins().contentProvider("cssresource");
		requestsList = new ArrayList<String>();
	}
	
	/* ASPECT LEVEL ASSETS */
	
	@Test
	public void assetsInADefaultAspectThemeCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceived("cssresource/aspect_default/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInADefaultAspectThemeCanBeRequestedFromWorkbench() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(workbench).requestReceived("cssresource/aspect_default/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInADefaultAspectResourcesCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(aspect).hasBeenCreated()
    		.and(aspect).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceived("cssresource/aspect_default/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInADefaultAspectAreIncludedInPossibleDevRequests() throws Exception
 	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
			.and(aspect).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
        when(cssResourcePlugin).getPossibleDevRequests(aspect, requestsList);
        thenRequests(requestsList).entriesEqual(
        		"cssresource/aspect_default/resources/dir1/dir2/someFile.txt",
        		"cssresource/aspect_default/theme_myTheme/dir1/dir2/someFile.txt"
		);
 	}
	
	@Test
	public void assetsInADefaultAspectAreIncludedInPossibleProdRequests() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(aspect).hasBeenCreated()
    		.and(aspect).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
    		.and(aspect).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(cssResourcePlugin).getPossibleProdRequests(aspect, requestsList);
		thenRequests(requestsList).entriesEqual(
				"cssresource/aspect_default/resources/dir1/dir2/someFile.txt",
				"cssresource/aspect_default/theme_myTheme/dir1/dir2/someFile.txt"
				
				
		);
	}
	
	@Test
	public void assetsInABladesetThemeCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
			.and(bladeset).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceived("cssresource/bladeset_bs/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladesetResourcesCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(bladeset).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceived("cssresource/bladeset_bs/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladesetAreIncludedInPossibleDevRequests() throws Exception
 	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
			.and(bladeset).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
			.and(bladeset).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
        when(cssResourcePlugin).getPossibleDevRequests(aspect, requestsList);
        thenRequests(requestsList).entriesEqual(
        		"cssresource/bladeset_bs/resources/dir1/dir2/someFile.txt",
        		"cssresource/bladeset_bs/theme_myTheme/dir1/dir2/someFile.txt"
		);
 	}
	
	@Test
	public void assetsInABladesetAreIncludedInPossibleProdRequests() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(bladeset).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
    		.and(bladeset).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(cssResourcePlugin).getPossibleProdRequests(aspect, requestsList);
		thenRequests(requestsList).entriesEqual(
				"cssresource/bladeset_bs/resources/dir1/dir2/someFile.txt",
				"cssresource/bladeset_bs/theme_myTheme/dir1/dir2/someFile.txt"
		);
	}
	
	
	/* BLADE LEVEL ASSETS */
	
	@Test
	public void assetsInABladeThemeCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
			.and(blade).hasBeenCreated()
			.and(blade).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents")
			.and(blade).containsFileWithContents("resources/someOtherFile.txt", "someOtherFile.txt contents");
		when(aspect).requestReceived("cssresource/bladeset_bs/blade_b1/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test 
	public void assetsInABladeResourcesCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
		.and(blade).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceived("cssresource/bladeset_bs/blade_b1/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladeAreIncludedInPossibleDevRequests() throws Exception
 	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
			.and(blade).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
			.and(blade).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
        when(cssResourcePlugin).getPossibleDevRequests(aspect, requestsList);
        thenRequests(requestsList).entriesEqual(
        		"cssresource/bladeset_bs/blade_b1/resources/dir1/dir2/someFile.txt",
        		"cssresource/bladeset_bs/blade_b1/theme_myTheme/dir1/dir2/someFile.txt"
		);
 	}
	
	@Test
	public void assetsInABladeAreIncludedInPossibleProdRequests() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
    		.and(blade).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
    		.and(blade).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(cssResourcePlugin).getPossibleProdRequests(aspect, requestsList);
		thenRequests(requestsList).entriesEqual(
				"cssresource/bladeset_bs/blade_b1/resources/dir1/dir2/someFile.txt",
				"cssresource/bladeset_bs/blade_b1/theme_myTheme/dir1/dir2/someFile.txt"
		);
	}
	
	/* WORKBENCH LEVEL ASSETS */
	//JT:TODO workbenches dont have themes
	@Test @Ignore
	public void assetsInABladeWorkbenchThemeCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
			.and(blade).hasBeenCreated()
			.and(workbench).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceived("cssresource/bladeset_bs/blade_b1/workbench/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladeWorkbenchResourcesCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
    		.and(workbench).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceived("cssresource/bladeset_bs/blade_b1/workbench/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladeWorkbenchAreIncludedInPossibleDevRequests() throws Exception
 	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
			.and(workbench).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
			.and(workbench).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
        when(cssResourcePlugin).getPossibleDevRequests(aspect, requestsList);
        thenRequests(requestsList).entriesEqual(
        		"cssresource/bladeset_bs/blade_b1/workbench/resources/dir1/dir2/someFile.txt",
        		"cssresource/bladeset_bs/blade_b1/workbench/theme_myTheme/dir1/dir2/someFile.txt"
		);
 	}
	
	@Test
	public void assetsInABladeWorkbenchAreIncludedInPossibleProdRequests() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
    		.and(workbench).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
    		.and(workbench).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(cssResourcePlugin).getPossibleProdRequests(aspect, requestsList);
		thenRequests(requestsList).entriesEqual(
				"cssresource/bladeset_bs/blade_b1/workbench/resources/dir1/dir2/someFile.txt",
				"cssresource/bladeset_bs/blade_b1/workbench/theme_myTheme/dir1/dir2/someFile.txt"
		);
	}
	
	//TODO: not sure about the request URL - that is what is sent from the browser but other tests only
	// seem to use a part of the URL. I guess this way is prone to brittleness
	// But surely we want to test the actual URLs sent from the browser ????
	@Test
	public void assetsInAnAspectThemeInheritedByABladeWorkbenchCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
    		.and(aspect).containsFileWithContents("themes/common/someFile.txt", "someFile.txt contents");
		when(app).requestReceived("workbench/bs1/b1/v/dev/cssresource/aspect_default/theme_common/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	/* LIBRARY LEVEL ASSETS */
	
	@Test
	public void assetsInAnSDKLibraryCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceived("cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInAnSDKLibraryCanBeRequestedFromAWorkbenchScope() throws Exception
	{
		given(app).hasBeenCreated()
			.and(workbench).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(workbench).requestReceived("cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsCanHaveAEncodedSpaceInTheirPath() throws Exception
	{
		given(app).hasBeenCreated()
			.and(workbench).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("some dir/another dir/someFile.txt", "someFile.txt contents");
		when(workbench).requestReceived("cssresource/lib_sdkLib/resources/some%20dir/another%20dir/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
}
