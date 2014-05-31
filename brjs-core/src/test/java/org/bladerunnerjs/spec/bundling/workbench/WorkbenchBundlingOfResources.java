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
import org.junit.Test;

public class WorkbenchBundlingOfResources extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private Blade blade2;
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
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			blade2 = bladeset.blade("b2");
			workbench = blade.workbench();
			workbenchTemplate = brjs.template("workbench");
			// workbench setup
			given(workbenchTemplate).containsFileWithContents("index.html", "<@css.bundle theme='standard'@/>")
				.and(workbenchTemplate).containsFolder("resources")
				.and(workbenchTemplate).containsFolder("src");
	}
	 
	// C S S
	@Test
	public void workbenchesLoadCssFromTheAspectLevel() throws Exception
	{
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).containsFileWithContents("/themes/standard/style.css", "ASPECT theme content")
			.and(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(bladeset).containsFileWithContents("/themes/standard/style.css", "BLADESET theme content")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.Class1", "appns.Class1")
			.and(blade).containsFileWithContents("themes/standard/style.css", "BLADE theme content")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1");	
		when(workbench).requestReceived("css/standard/bundle.css", response);
		then(response).containsOrderedTextFragments("BLADESET theme content",
													"BLADE theme content",
													"ASPECT theme content");
	}
	
	@Test
	public void workbenchesAlwaysLoadsCommonCssFromTheAspectLevel() throws Exception
	{
		given(aspect).containsFileWithContents("themes/standard/style.css", "ASPECT theme content");
		when(workbench).requestReceived("css/standard/bundle.css", response);
		then(response).containsText("ASPECT theme content");
	}
	
	@Test
	public void assetsFromAnotherBladeArentLoadedIfTheAspectResourcesDependsOnThem() throws Exception
	{
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("resources/someFile.xml", "appns.bs.b2.Class1")
			.and(blade2).hasClass("appns/bs/b2/Class1")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1");	
		when(workbench).requestReceived("js/dev/combined/bundle.js", response);		
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	
	
}
