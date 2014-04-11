package org.bladerunnerjs.spec.bundling.workbench;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.Workbench;
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
	private JsLib thirdpartyLib, brjsLib, appLib;
	private NamedDirNode workbenchTemplate;
	private StringBuffer response;
	private JsLib bootstrapLib;
	
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
		appLib = app.jsLib("appLib");
		
		bootstrapLib = brjs.sdkNonBladeRunnerLib("br-bootstrap");
		
		response = new StringBuffer();

		given(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'")
			.and(workbenchTemplate).containsFolder("resources")
			.and(workbenchTemplate).containsFolder("src");
	}
	// ------------------------------------ J S ---------------------------------------
	@Test
	public void workbenchPageCanBundleAnSdkJsLibraryClass() throws Exception {
		given(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "exports: thirdpartyLib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.lib = { }")
			.and(workbench).indexPageRequires("thirdparty-lib1");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void workbenchBundlesAspectJSClassFilesWhenReferenced() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1")
			.and(workbench).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("appns.Class1")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void weBundleBootstrapSrcInASubDir() throws Exception {
		given(workbench).hasClass("appns/Class1")
			.and(workbench).indexPageRefersTo("appns.Class1")
			.and(bootstrapLib).hasBeenCreated()
			.and(bootstrapLib).containsFileWithContents("library.manifest", "js: sub/dir/bootstrap.js\n"+"exports: lib")
			.and(bootstrapLib).containsFileWithContents("sub/dir/bootstrap.js", "// this is bootstrap");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("// br-bootstrap");
		then(response).containsText("// this is bootstrap"); 
	}
	
	@Test
	public void weBundleBootstrapBeforeOtherLibsFromTheApp() throws Exception {
		given(workbench).hasClass("appns/Class1")
    		.and(workbench).indexPageRequires("appLib")
    		.and(appLib).hasBeenCreated()
    		.and(appLib).containsFileWithContents("library.manifest", "js: lib.js\n"+"exports: lib")
    		.and(appLib).containsFileWithContents("lib.js", "// this is appLib")
    		.and(bootstrapLib).hasBeenCreated()
    		.and(bootstrapLib).containsFileWithContents("library.manifest", "js: sub/dir/bootstrap.js\n"+"exports: lib")
    		.and(bootstrapLib).containsFileWithContents("sub/dir/bootstrap.js", "// this is bootstrap");
    	when(app).requestReceived("/bs-bladeset/blades/b1/workbench/js/dev/en_GB/combined/bundle.js", response);
    	then(response).containsOrderedTextFragments("// br-bootstrap",
    			"// appLib" );
	}

	
	// ----------------------------------- C S S  -------------------------------------
 	@Test
 	public void aspectCssFilesAreBundledInTheWorkbench() throws Exception {
		given(standardAspectTheme).containsFileWithContents("style.css", "ASPECT theme content")
			.and(aspect).hasClass("appns/Class1")
    		.and(workbench).indexPageRefersTo("appns.Class1");
 		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/css/standard/bundle.css", response);
 		then(response).containsText("ASPECT theme content");
 	}
	
 	@Test
 	public void aspectCssFilesAreBundledInTheWorkbenchEvenIfAspectSrcIsntUsed() throws Exception {
 		given(standardAspectTheme).containsFileWithContents("style.css", "ASPECT theme content")
        	.and(workbench).hasClass("appns/workbench/Class1")
        	.and(workbench).indexPageRefersTo("appns.workbench.Class1");
 		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/css/standard/bundle.css", response);
 		then(response).containsText("ASPECT theme content");
 	}
 	
 	@Test
 	public void bladesetCssFilesAreBundledWhenReferencedInTheWorkbench() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(standardBladesetTheme).containsFileWithContents("style.css", "BLADESET theme content")
			.and(workbench).indexPageRefersTo("appns.bs.Class1");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/css/standard/bundle.css", response);
 		then(response).containsText("BLADESET theme content");
 	}
	 
 	@Test
 	public void bladeCssFilesAreBundledWhenReferencedInTheWorkbench() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(standardBladeTheme).containsFileWithContents("style.css", "BLADE theme content")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/css/standard/bundle.css", response);
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
	
	@Test
	public void bladesCanNotDependOnWorkbenchClasses() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(workbench).hasClass("appns.WorkbenchClass")
			.and(blade).classDependsOn("appns.bs.b1.BladeClass", "appns.WorkbenchClass")
			.and(workbench).indexPageRefersTo("appns.bs.b1.BladeClass");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("appns.bs.b1.BladeClass =")
			.and(response).doesNotContainText("appns.WorkbenchClass =");
	}
}
