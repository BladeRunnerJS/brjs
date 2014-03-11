package org.bladerunnerjs.spec.plugin.bundler.cssresource;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.Workbench;
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
	private Theme standardAspetTheme;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			standardAspetTheme = aspect.theme("standard");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
			sdkJsLib = brjs.sdkLib("sdkLib");
	}
	
//	.accepts("cssresource/theme_<theme>/<resourcePath>").as("aspect-request")
//	.and("cssresource/bladeset_<bladeset>/theme_<theme>/<resourcePath>").as("bladeset-request")
//	.and("cssresource/bladeset_<bladeset>/blade_<blade>/theme_<theme>/<resourcePath>").as("blade-request")
//	.and("cssresource/bladeset_<bladeset>/blade_<blade>/workbench/<resourcePath>").as("blade-workbench-request")
//	.and("cssresource/lib_<lib>/<resourcePath>").as("lib-request")
	
	@Test
	public void assetsInADefaultAspectThemeCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(app).requestReceived("/default-aspect/cssresource/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladesetThemeCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
			.and(bladeset).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(app).requestReceived("/default-aspect/cssresource/bladeset_bs/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladeThemeCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
			.and(blade).hasBeenCreated()
			.and(blade).containsFileWithContents("themes/myTheme/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(app).requestReceived("/default-aspect/cssresource/bladeset_bs/blade_b1/theme_myTheme/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInABladeWorkbenchCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(bladeset).hasBeenCreated()
			.and(blade).hasBeenCreated()
			.and(workbench).containsFileWithContents("resources/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(app).requestReceived("/default-aspect/cssresource/bladeset_bs/blade_b1/workbench/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	
	
	
	@Test
	public void assetsInAnSDKLibraryCanBeRequested() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(sdkJsLib).containsFileWithContents("resources/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(app).requestReceived("/default-aspect/cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
	@Test
	public void assetsInAnSDKLibraryCanBeRequestedFromAWorkbench() throws Exception
	{
		given(app).hasBeenCreated()
			.and(workbench).hasBeenCreated()
			.and(sdkJsLib).containsFileWithContents("resources/dir1/dir2/someFile.txt", "someFile.txt contents");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/cssresource/lib_sdkLib/resources/dir1/dir2/someFile.txt", response);
		then(response).textEquals("someFile.txt contents");
	}
	
}
