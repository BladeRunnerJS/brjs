package org.bladerunnerjs.spec.bundling.workbench;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.BladesetWorkbench;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class BladesetWorkbenchBundlingTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private BladesetWorkbench workbench;
	private JsLib thirdpartyLib, brjsLib, appLib;
	private NamedDirNode workbenchTemplate;
	private StringBuffer response;
	private JsLib bootstrapLib;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();

		app = brjs.app("app1");
		aspect = app.aspect("default");
		bladeset = app.bladeset("bs");
		blade = bladeset.blade("b1");
		workbench = bladeset.workbench();
		workbenchTemplate = brjs.template("workbench");
		brjsLib = brjs.sdkLib("br");
		thirdpartyLib = brjs.sdkLib("thirdparty-lib1");
		appLib = app.jsLib("appLib");
		bootstrapLib = brjs.sdkLib("br-bootstrap");
		
		response = new StringBuffer();

		given(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'")
			.and(workbenchTemplate).containsFolder("resources")
			.and(workbenchTemplate).containsFolder("src");
	}
	
	// ----------------------------------- H T M L  -------------------------------------
	@Test
	public void workbenchCanBundleSdkLibHtmlResources() throws Exception {
		given(brjsLib).hasBeenCreated()
			.and(brjsLib).hasNamespacedJsPackageStyle()
			.and(brjsLib).containsResourceFileWithContents("html/view.html", "<div id='br.tree-view'></div>")
			.and(brjsLib).hasClass("br.workbench.ui.Workbench")
			.and(workbench).containsResourceFileWithContents("workbench-view.html", "<div id='appns.bs.b1.workbench-view'></div>")
			.and(workbench).indexPageRefersTo("br.workbench.ui.Workbench");
		when(workbench).requestReceivedInDev("html/bundle.html", response);
		then(response).containsOrderedTextFragments("<div id='br.tree-view'></div>",
													"<div id='appns.bs.b1.workbench-view'></div>");							
	}
	
	@Test
	public void bladesetsCanNotDependOnWorkbenchClasses() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(workbench).hasClass("appns.WorkbenchClass")
			.and(bladeset).classDependsOn("appns.bs.b1.BladesetClass", "appns.WorkbenchClass")
			.and(workbench).indexPageRefersTo("appns.bs.b1.BladesetClass");
		when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.bs.b1.BladesetClass =")
			.and(response).doesNotContainText("appns.WorkbenchClass =");
	}
}
