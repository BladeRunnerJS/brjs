package org.bladerunnerjs.spec.bundling.workbench;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class WorkbenchBundlingOfResources extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private Theme standardAspectTheme, standardBladesetTheme, standardBladeTheme;
	private StringBuffer response = new StringBuffer();
	private Workbench workbench;
	private NamedDirNode workbenchTemplate;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			standardAspectTheme = aspect.theme("standard");
			bladeset = app.bladeset("bs");
			standardBladesetTheme = bladeset.theme("standard");
			blade = bladeset.blade("b1");
			standardBladeTheme = blade.theme("standard");
			workbench = blade.workbench();
			workbenchTemplate = brjs.template("workbench");
			
			// workbench setup
			given(workbenchTemplate).containsFileWithContents("index.html", "<@css.bundle theme='standard'@/>")
				.and(workbenchTemplate).containsFolder("resources")
				.and(workbenchTemplate).containsFolder("src");
	}
	 
	// C S S
	// TODO this test seems to be serving up the CSS in the wrong order
	@Ignore
	@Test
	public void workbenchesLoadCssFromTheAspectLevel() throws Exception
	{
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(standardAspectTheme).containsFileWithContents("style.css", "ASPECT theme content")
			.and(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(standardBladesetTheme).containsFileWithContents("style.css", "BLADESET theme content")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.Class1", "appns.Class1")
			.and(standardBladeTheme).containsFileWithContents("style.css", "BLADE theme content")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1");	
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/css/standard/bundle.css", response);
		then(response).containsOrderedTextFragments("BLADESET theme content",
													"BLADE theme content",
													"ASPECT theme content");
	}
}
