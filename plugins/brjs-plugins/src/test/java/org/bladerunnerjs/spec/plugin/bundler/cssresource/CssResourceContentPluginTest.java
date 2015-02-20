package org.bladerunnerjs.spec.plugin.bundler.cssresource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.BladesetWorkbench;
import org.bladerunnerjs.model.BladeWorkbench;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CssResourceContentPluginTest extends SpecTest {
	private App app;
	private StringBuffer response = new StringBuffer();
	private OutputStream binaryResponse;
	private File binaryResponseFile;
	private JsLib sdkJsLib;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private BladeWorkbench bladeWorkbench;
	private BladesetWorkbench bladesetWorkbench;
	private ContentPlugin cssResourcePlugin;
	private List<String> requestsList;
	private Aspect defaultAspect;
	private Blade bladeInDefaultBladeset;
	private File targetDir;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			defaultAspect = app.defaultAspect();
			bladeset = app.bladeset("bs");
			bladesetWorkbench = bladeset.workbench();
			blade = bladeset.blade("b1");
			bladeWorkbench = blade.workbench();
			sdkJsLib = brjs.sdkLib("sdkLib");
			bladeInDefaultBladeset = app.defaultBladeset().blade("b1");
			targetDir = FileUtils.createTemporaryDirectory( this.getClass() );
		
		binaryResponseFile = FileUtils.createTemporaryFile( this.getClass() );
		binaryResponse = new FileOutputStream(binaryResponseFile);
		cssResourcePlugin = brjs.plugins().contentPlugin("cssresource");
		requestsList = new ArrayList<String>();
	}
	
	/* ASPECT LEVEL ASSETS */
	
	@Test
	public void assetsInADefaultAspectThemeCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInDev("cssresource/aspect_default/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInADefaultAspectThemeCanBeRequestedFromWorkbench() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(bladeWorkbench).requestReceivedInDev("cssresource/aspect_default/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInADefaultAspectResourcesCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(aspect).hasBeenCreated()
    		.and(aspect).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInDev("cssresource/aspect_default_resource/resources/dir1/dir2/someFile.txt", response);
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
        		"cssresource/aspect_default_resource/resources/dir1/dir2/someFile.txt",
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
				"cssresource/aspect_default_resource/resources/dir1/dir2/someFile.txt",
				"cssresource/aspect_default/theme_myTheme/dir1/dir2/someFile.txt"
		);
	}
	
	@Test
	public void assetsInABladesetThemeCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
			.and(bladeset).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInDev("cssresource/bladeset_bs/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladesetResourcesCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(bladeset).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInDev("cssresource/bladeset_bs_resource/resources/dir1/dir2/someFile.txt", response);
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
        		"cssresource/bladeset_bs_resource/resources/dir1/dir2/someFile.txt",
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
				"cssresource/bladeset_bs_resource/resources/dir1/dir2/someFile.txt",
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
		when(aspect).requestReceivedInDev("cssresource/bladeset_bs/blade_b1/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test 
	public void assetsInABladeResourcesCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
		.and(blade).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInDev("cssresource/bladeset_bs/blade_b1_resource/resources/dir1/dir2/someFile.txt", response);
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
        		"cssresource/bladeset_bs/blade_b1_resource/resources/dir1/dir2/someFile.txt",
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
				"cssresource/bladeset_bs/blade_b1_resource/resources/dir1/dir2/someFile.txt",
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
			.and(bladeWorkbench).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInDev("cssresource/bladeset_bs/blade_b1/workbench/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladeWorkbenchResourcesCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
    		.and(bladeWorkbench).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInDev("cssresource/bladeset_bs/blade_b1/workbench_resource/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladeWorkbenchAreIncludedInPossibleDevRequests() throws Exception
 	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
			.and(bladeWorkbench).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
			.and(bladeWorkbench).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
        when(cssResourcePlugin).getPossibleDevRequests(bladeWorkbench, requestsList);
        thenRequests(requestsList).entriesEqual(
        		"cssresource/bladeset_bs/blade_b1/workbench_resource/resources/dir1/dir2/someFile.txt",
        		"cssresource/bladeset_bs/blade_b1/workbench/theme_myTheme/dir1/dir2/someFile.txt"
		);
 	}
	
	@Test
	public void assetsInABladeWorkbenchAreNotIncludedInPossibleProdAspectRequests() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
    		.and(bladeWorkbench).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
    		.and(bladeWorkbench).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(cssResourcePlugin).getPossibleProdRequests(aspect, requestsList);
		thenRequests(requestsList).entriesDoNotContain(
				"cssresource/bladeset_bs/blade_b1/workbench_resource/resources/dir1/dir2/someFile.txt",
				"cssresource/bladeset_bs/blade_b1/workbench/theme_myTheme/dir1/dir2/someFile.txt"
		);
	}
	
	@Test
	public void assetsInABladesetWorkbenchResourcesCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(bladesetWorkbench).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInDev("cssresource/bladeset_bs/workbench_resource/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladesetWorkbenchAreNotIncludedInPossibleProdAspectRequests() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(bladeset).hasBeenCreated()
    		.and(bladesetWorkbench).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
    		.and(bladesetWorkbench).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(cssResourcePlugin).getPossibleProdRequests(aspect, requestsList);
		thenRequests(requestsList).entriesDoNotContain(
				"cssresource/bladeset_bs/workbench_resource/resources/dir1/dir2/someFile.txt",
				"cssresource/bladeset_bs/workbench/theme_myTheme/dir1/dir2/someFile.txt"
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
		when(app).requestReceived("bs1/b1/workbench/v/dev/cssresource/aspect_default/theme_common/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	/* LIBRARY LEVEL ASSETS */
	
	@Test
	public void assetsInAnSDKLibraryCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInDev("cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInAnSDKLibraryCanBeRequestedFromAWorkbenchScope() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeWorkbench).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(bladeWorkbench).requestReceivedInDev("cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsCanHaveAEncodedSpaceInTheirPath() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeWorkbench).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("some dir/another dir/someFile.txt", "someFile.txt contents");
		when(bladeWorkbench).requestReceivedInDev("cssresource/lib_sdkLib/resources/some%20dir/another%20dir/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void imagesArentCorrupt() throws Exception
	{
		given(app).hasBeenCreated()
    		.and(aspect).hasBeenCreated()
    		.and(aspect).containsFileCopiedFrom("resources/br-logo.png", "src/test/resources/br-logo.png");
    	when(aspect).requestReceivedInDev("cssresource/aspect_default_resource/resources/br-logo.png", binaryResponse);
    	then(binaryResponseFile).sameAsFile("src/test/resources/br-logo.png");
	}
	
	@Test
	public void assetsInABRSDKLibraryAreAvailableInDev() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		then(aspect).devRequestsForContentPluginsAre("cssresource", "cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt");
	}
	
	@Test
	public void assetsInBRSdkLibraryInDevHaveCorrectContent() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInDev("cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABRSDKLibraryAreAvailableInProd() throws Exception
	{
		given(app).hasBeenCreated()
		.and(aspect).hasBeenCreated()
		.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		then(aspect).prodRequestsForContentPluginsAre("cssresource", "cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt");
	}
	
	@Test
	public void assetsInABRSdkLibraryInProdHaveCorrectContent() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInProd("cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}	
	
	@Test
	public void assetsInAThirdpartySDKLibraryAreAvailableInDev() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
			.and(sdkJsLib).containsFileWithContents("thirdparty-lib.manifest", "depends:");
		then(aspect).devRequestsForContentPluginsAre("cssresource", "cssresource/lib_sdkLib/thirdparty-lib.manifest, cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt");
	}
	
	@Test
	public void assetsInAThirdpartySdkLibraryInDevHaveCorrectContent() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
			.and(sdkJsLib).containsFileWithContents("thirdparty-lib.manifest", "depends:");
		when(aspect).requestReceivedInDev("cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInAThirdpartySDKLibraryAreAvailableInProd() throws Exception
	{
		given(app).hasBeenCreated()
		.and(aspect).hasBeenCreated()
		.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
		.and(sdkJsLib).containsFileWithContents("thirdparty-lib.manifest", "depends:");
		then(aspect).prodRequestsForContentPluginsAre("cssresource", "cssresource/lib_sdkLib/thirdparty-lib.manifest, cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt");
	}
	
	@Test
	public void assetsInAThirdpartySdkLibraryInProdHaveCorrectContent() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(sdkJsLib).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents")
			.and(sdkJsLib).containsFileWithContents("thirdparty-lib.manifest", "depends:");
		when(aspect).requestReceivedInProd("cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInDefaultBladesetBladeCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeInDefaultBladeset).hasBeenCreated()
			.and(bladeInDefaultBladeset).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(aspect).requestReceivedInProd("cssresource/bladeset_default/blade_b1_resource/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInDefaultAspectCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(defaultAspect).indexPageHasContent("")
			.and(defaultAspect).containsResourceFileWithContents("dir1/dir2/someFile.txt", "someFile.txt contents");
		when(defaultAspect).requestReceivedInProd("cssresource/aspect_default_resource/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void pathsIgnoredViaConfigAreNotServed() throws Exception {
		given(app).hasBeenCreated()
    		.and(defaultAspect).indexPageHasContent("")
    		.and(defaultAspect).containsResourceFileWithContents(".git", ".git contents")
    		.and(brjs.bladerunnerConf()).hasIgnoredPaths(".git");
    	when(defaultAspect).requestReceivedInProd("cssresource/aspect_default_resource/resources/.git", response);
    	then(aspect).prodRequestsForContentPluginsAre("cssresource", "")
    		.and(aspect).devRequestsForContentPluginsAre("cssresource", "")
    		.and(exceptions).verifyException(FileNotFoundException.class, "apps/app1/resources/.git");
	}
	
	@Test
	public void pathsInJsLibsIgnoredViaConfigAreNotServed() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).indexPageHasContent("")
    		.and(sdkJsLib).containsResourceFileWithContents(".git", ".git contents")
    		.and(brjs.bladerunnerConf()).hasIgnoredPaths(".git");
    	when(defaultAspect).requestReceivedInProd("cssresource/lib_sdkLib/.git", response);
    	then(aspect).prodRequestsForContentPluginsAre("cssresource", "")
    		.and(aspect).devRequestsForContentPluginsAre("cssresource", "")
    		.and(exceptions).verifyException(FileNotFoundException.class, "sdk/libs/javascript/sdkLib/.git");
	}
	
	@Test // This is to protect against .less or .sass files referencing the file where we wont detect it
	public void cssResourcesWithCommonExtensionsAreIncludedInContentPathsEvenIfTheyArentUsed() throws Exception {
		given(defaultAspect).indexPageHasContent("")
			.and(defaultAspect).containsFiles("themes/common/unusedFile.png")
			.and(brjs).localeForwarderHasContents("")
			.and(brjs).hasProdVersion("1234");
		then(defaultAspect).usedProdContentPathsForPluginsAre("cssresource", "cssresource/aspect_default/theme_common/unusedFile.png");
	}
	
	@Test
	public void onlyCssResourceBundlesUsedFromCssFilesAreReturnedAsContentPaths() throws Exception {
		given(defaultAspect).indexPageHasContent("")
			.and(defaultAspect).containsFiles("themes/common/usedFile.ext", "resources/css/usedFile.ext", "resources/css/unusedFile.ext", "resources/some-dir/unusedFile.ext")
			.and(defaultAspect).containsFileWithContents("themes/common/style.css", ".style { background:url('usedFile.ext'); background:url('../../resources/css/usedFile.ext');")
			.and(brjs).localeForwarderHasContents("")
			.and(brjs).hasProdVersion("1234");
		then(defaultAspect).usedProdContentPathsForPluginsAre("cssresource", "cssresource/aspect_default_resource/resources/css/usedFile.ext", "cssresource/aspect_default/theme_common/usedFile.ext");
	}
	
	@Test
	public void cssResourcesUsedInIndexPagesShouldBeIncludedInFilteredContentPaths() throws Exception {
		given(defaultAspect).indexPageHasContent(".style { background:url('v/1234/cssresource/aspect_default_resource/resources/css/usedFile.ext') }")
			.and(defaultAspect).containsFiles("themes/common/usedFile.ext", "resources/css/usedFile.ext", "resources/css/unusedFile.ext", "resources/some-dir/unusedFile.ext")
			.and(brjs).localeForwarderHasContents("")
			.and(brjs).hasProdVersion("1234");
		then(defaultAspect).usedProdContentPathsForPluginsAre("cssresource", "cssresource/aspect_default_resource/resources/css/usedFile.ext");
	}
	
	@Test
	public void onlyCssResourceBundlesUsedFromCssFilesArePresentInTheBuiltArtifact() throws Exception {
		given(defaultAspect).indexPageHasContent("")
			.and(defaultAspect).containsFiles("themes/common/usedFile.ext", "resources/css/usedFile.ext", "resources/css/unusedFile.ext", "resources/some-dir/unusedFile.ext")
    		.and(defaultAspect).containsFileWithContents("themes/common/style.css", ".style { background:url('usedFile.ext'); background:url('../../resources/css/usedFile.ext');")
    		.and(brjs).localeForwarderHasContents("")
    		.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("v/1234/cssresource/aspect_default_resource/resources/css/usedFile.ext")
			.and(targetDir).containsFile("v/1234/cssresource/aspect_default/theme_common/usedFile.ext")
			.and(targetDir).doesNotContainFile("v/1234/cssresource/aspect_default_resource/resources/css/unusedFile.ext")
			.and(targetDir).doesNotContainFile("v/1234/cssresource/aspect_default_resource/resources/some-dir/unusedFile.ext")
			.and(targetDir).doesNotContainFile("v/1234/cssresource/aspect_default/theme_common/style.css");
	}
	
}
