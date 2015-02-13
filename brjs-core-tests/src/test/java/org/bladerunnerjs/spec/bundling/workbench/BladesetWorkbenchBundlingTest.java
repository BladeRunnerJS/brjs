package org.bladerunnerjs.spec.bundling.workbench;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.BladesetWorkbench;
import org.bladerunnerjs.model.NamedDirNode;
import org.junit.Before;
import org.junit.Test;

public class BladesetWorkbenchBundlingTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset1, bladeset2, defaultBladeset;
	private Blade blade1, blade2, blade3, bladeInDefaultBladeset;
	private BladesetWorkbench bladeset1Workbench, defaultBladesetWorkbench;
	private JsLib brjsLib;
	private NamedDirNode workbenchTemplate;
	private StringBuffer response;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();

		app = brjs.app("app1");
		aspect = app.aspect("default");
		bladeset1 = app.bladeset("bs1");
		bladeset2 = app.bladeset("bs2");
		blade1 = bladeset1.blade("b1");
		blade2 = bladeset1.blade("b2");
		blade3 = bladeset2.blade("b3");
		bladeset1Workbench = bladeset1.workbench();
		defaultBladeset = app.defaultBladeset();
		bladeInDefaultBladeset = defaultBladeset.blade("b4");
		defaultBladesetWorkbench = defaultBladeset.workbench();
		workbenchTemplate = brjs.sdkTemplateGroup("default").template("workbench");
		brjsLib = brjs.sdkLib("br");
		
		response = new StringBuffer();

		given(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'")
			.and(workbenchTemplate).containsFolder("resources")
			.and(workbenchTemplate).containsFolder("src");
	}
	
	@Test
	public void workbenchMayNotReferenceAJsFromTheDefaultAspect() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(bladeset1Workbench).indexPageRefersTo("appns.Class1");
		when(bladeset1Workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("appns.Class1")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void codeFromBladesNotInTheBladesetCannotBeBundled() throws Exception {
		given(blade1).hasNamespacedJsPackageStyle()
			.and(blade1).hasClass("appns.bs1.b1.Class1")
			.and(blade3).hasNamespacedJsPackageStyle()
			.and(blade3).hasClass("appns.bs2.b3.Class2")
			.and(bladeset1Workbench).indexPageRefersTo("appns.bs1.b1.Class1");
		when(bladeset1Workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.bs1.b1.Class1")
			.and(response).doesNotContainText("appns.bs2.b3.Class2")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void tagsInTheBladesetWorkbenchAreReplaced() throws Exception {
		given(bladeset1Workbench).containsFileWithContents("index.html", "<@css.bundle@/>")
			.and(bladeset1Workbench).containsFiles("themes/common/style.css", "themes/common/style_en.css", "themes/common/style_en_GB.css");
		when(bladeset1Workbench).pageLoaded(response, "en");
		then(response).containsOrderedTextFragments(
			"<link rel=\"stylesheet\" href=\"v/dev/css/common/bundle.css\"/>",
			"<link rel=\"stylesheet\" href=\"v/dev/css/common_en/bundle.css\"/>");
	}
	
	@Test
	public void workbenchBundlesCodeFromTwoBladesInsideTheWorkbench() throws Exception {
		given(blade1).hasNamespacedJsPackageStyle()
			.and(blade1).hasClass("appns.bs1.b1.Class1")
			.and(blade2).hasNamespacedJsPackageStyle()
			.and(blade2).hasClass("appns.bs1.b2.Class2")
			.and(bladeset1Workbench).indexPageRefersTo("appns.bs1.b1.Class1", "appns.bs1.b2.Class2");
		when(bladeset1Workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.bs1.b1.Class1")
			.and(response).containsText("appns.bs1.b2.Class2")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void workbenchCanBundleSdkLibHtmlResources() throws Exception {
		given(brjsLib).hasBeenCreated()
			.and(brjsLib).hasNamespacedJsPackageStyle()
			.and(brjsLib).containsResourceFileWithContents("html/view.html", "<div id='br.tree-view'></div>")
			.and(brjsLib).hasClass("br.workbench.ui.Workbench")
			.and(bladeset1Workbench).containsResourceFileWithContents("workbench-view.html", "<div id='appns.bs.b1.workbench-view'></div>")
			.and(bladeset1Workbench).indexPageRefersTo("br.workbench.ui.Workbench");
		when(bladeset1Workbench).requestReceivedInDev("html/bundle.html", response);
		then(response).containsOrderedTextFragments("<div id='br.tree-view'></div>",
													"<div id='appns.bs.b1.workbench-view'></div>");							
	}
	
	@Test
	public void bladesetsCanNotDependOnWorkbenchClasses() throws Exception {
		given(bladeset1).hasNamespacedJsPackageStyle()
			.and(bladeset1Workbench).hasClass("appns.WorkbenchClass")
			.and(bladeset1).classDependsOn("appns.bs1.b1.BladesetClass", "appns.WorkbenchClass")
			.and(bladeset1Workbench).indexPageRefersTo("appns.bs1.b1.BladesetClass");
		when(bladeset1Workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.bs1.b1.BladesetClass =")
			.and(response).doesNotContainText("appns.WorkbenchClass =");
	}
	
	@Test
	public void theDefaultBladesetCanHaveAWorkbench() throws Exception {
		given(bladeInDefaultBladeset).hasNamespacedJsPackageStyle()
			.and(bladeInDefaultBladeset).hasClass("appns.b4.Class4")
			.and(defaultBladesetWorkbench).indexPageRefersTo("appns.b4.Class4");
		when(defaultBladesetWorkbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.b4.Class4")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
}
