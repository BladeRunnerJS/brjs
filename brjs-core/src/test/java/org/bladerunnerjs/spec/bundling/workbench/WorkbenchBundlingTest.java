package org.bladerunnerjs.spec.bundling.workbench;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class WorkbenchBundlingTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private Theme standardAspectTheme, standardBladesetTheme, standardBladeTheme;
	private Bladeset bladeset;
	private Blade blade;
	private Workbench workbench;
	private JsLib thirdpartyLib, brjsLib;
	private NamedDirNode workbenchTemplate;
	private StringBuffer response;
	
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
		brjsLib = brjs.sdkLib("br");
		thirdpartyLib = brjs.sdkNonBladeRunnerLib("thirdparty-lib1");
		
		response = new StringBuffer();

		given(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'")
			.and(workbenchTemplate).containsFolder("resources")
			.and(workbenchTemplate).containsFolder("src");
	}
	// ------------------------------------ J S ---------------------------------------
	@Test
	public void workbenchPageCanBundleAnSdkJsLibraryClass() throws Exception {
		given(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "depends:")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.lib = { }")
			.and(workbench).indexPageRefersTo("thirdparty-lib1");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void workbenchPageDoesNotBundleAspectJSClassFilesWhenReferenced() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1")
			.and(workbench).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyException(UnresolvableRequirePathException.class, "appns/Class1")
			.whereTopLevelExceptionIs(ContentProcessingException.class);
	}

	
	// ----------------------------------- C S S  -------------------------------------
	// TODO enable when we work on CSS Bundler
	@Ignore 
 	@Test
 	public void aspectCssFilesAreBundledInTheWorkbench() throws Exception {
		given(standardAspectTheme).containsFileWithContents("style.css", "ASPECT theme content");
 		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/css/standard_css.bundle", response);
 		then(response).containsText("ASPECT theme content");
 	}
	
	@Ignore 
 	@Test
 	public void bladesetCssFilesAreBundledWhenReferencedInTheWorkbench() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(standardBladesetTheme).containsFileWithContents("style.css", "BLADESET theme content")
			.and(workbench).indexPageRefersTo("appns.bs.Class1");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/css/standard_css.bundle", response);
 		then(response).containsText("BLADESET theme content");
 	}
	
	@Ignore 
 	@Test
 	public void bladeCssFilesAreBundledWhenReferencedInTheWorkbench() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(standardBladeTheme).containsFileWithContents("style.css", "BLADE theme content")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/css/standard_css.bundle", response);
 		then(response).containsText("BLADE theme content");
 	}
	
	// TODO This was the previous behaviour for bladerunner - this will now be opt-in?
	@Ignore
	@Test
	public void sdkLibCssFilesAreNotBundledAsCommonCssInTheWorkbenchWhenNotReferenced() throws Exception {

	}
	
	// ----------------------------------- H T M L  -------------------------------------
	@Ignore // This test should pass to prove that 
	@Test
	public void workbenchCanBundleSdkLibHtmlResources() throws Exception {
		given(brjsLib).hasBeenCreated()
			.and(brjsLib).hasNamespacedJsPackageStyle()
			.and(brjsLib).containsFileWithContents("resources/html/view.html", "<div id='tree-view'></div>")
			.and(brjsLib).hasClass("br.workbench.ui.Workbench")
			.and(workbench).containsFileWithContents("resources/workbench-view.html", "<div id='appns.bs.b1.workbench-view'></div>")
			.and(workbench).indexPageRefersTo("br.workbench.ui.Workbench");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/bundle.html", response);
		then(response).containsOrderedTextFragments("<div id='appns.bs.b1.workbench-view'></div>",
													"<div id='tree-view'></div>");
	}
	
	
}
