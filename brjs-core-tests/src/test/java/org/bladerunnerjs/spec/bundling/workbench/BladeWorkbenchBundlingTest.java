package org.bladerunnerjs.spec.bundling.workbench;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.model.exception.OutOfBundleScopeRequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.api.BladeWorkbench;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BladeWorkbenchBundlingTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private BladeWorkbench workbench;
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
		workbench = blade.workbench();
		workbenchTemplate = brjs.sdkTemplateGroup("default").template("workbench");
		brjsLib = brjs.sdkLib("br");
		thirdpartyLib = brjs.sdkLib("thirdparty-lib1");
		appLib = app.jsLib("appLib");
		bootstrapLib = brjs.sdkLib("br-bootstrap");
		
		response = new StringBuffer();

		given(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'")
			.and(workbenchTemplate).containsFolder("resources")
			.and(workbenchTemplate).containsFolder("src");
	}
	// ------------------------------------ J S ---------------------------------------
	@Test
	public void workbenchPageCanBundleAnSdkJsLibraryClass() throws Exception {
		given(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "exports: thirdpartyLib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.lib = { }")
			.and(workbench).indexPageRequires("thirdparty-lib1");
		when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("window.lib = { }")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void workbenchBundlesBladeJSClassFilesWhenReferenced() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1");
		when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.bs.b1.Class1")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void workbenchMayNotReferenceAJsFromTheDefaultAspect() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(workbench).indexPageRefersTo("appns.Class1");
		when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("appns.Class1")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void workbenchBundlesDontIncludeAspectJS() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1", "appns.Class1");
		when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainClasses("appns.Class1")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void weBundleBootstrapSrcInASubDir() throws Exception {
		given(workbench).hasClass("appns/Class1")
			.and(workbench).indexPageRefersTo("appns.Class1")
			.and(bootstrapLib).hasBeenCreated()
			.and(bootstrapLib).containsFileWithContents("thirdparty-lib.manifest", "js: sub/dir/bootstrap.js\n"+"exports: lib")
			.and(bootstrapLib).containsFileWithContents("sub/dir/bootstrap.js", "// this is bootstrap");
		when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("// br-bootstrap");
		then(response).containsText("// this is bootstrap"); 
	}
	
	@Test
	public void weBundleBootstrapBeforeOtherLibsFromTheApp() throws Exception {
		given(workbench).hasClass("appns/Class1")
    		.and(workbench).indexPageRequires("appLib")
    		.and(appLib).hasBeenCreated()
    		.and(appLib).containsFileWithContents("thirdparty-lib.manifest", "js: lib.js\n"+"exports: lib")
    		.and(appLib).containsFileWithContents("lib.js", "// this is appLib")
    		.and(bootstrapLib).hasBeenCreated()
    		.and(bootstrapLib).containsFileWithContents("thirdparty-lib.manifest", "js: sub/dir/bootstrap.js\n"+"exports: lib")
    		.and(bootstrapLib).containsFileWithContents("sub/dir/bootstrap.js", "// this is bootstrap");
    	when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).containsOrderedTextFragments("// br-bootstrap",
    			"// appLib" );
	}
	
	@Test
	public void outOfScopeExceptionIsThrownIfTheRequiredClassIsOutOfScope() throws Exception {
		given(aspect).hasClass("appns/App")
			.and(blade).classRequires("appns/bs/b1/Class1", "appns/App")
			.and(blade.workbench()).indexPageRequires("appns/bs/b1/Class1");
		when(blade.workbench()).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(OutOfBundleScopeRequirePathException.class, 
				"appns/App", "default-aspect/src/appns/App.js", BladeWorkbench.class.getSimpleName(),
				"apps/app1/bs-bladeset, apps/app1/bs-bladeset/blades/b1, apps/app1/bs-bladeset/blades/b1/workbench")
			.whereTopLevelExceptionIs(ContentProcessingException.class);
	}
	
	@Test
	public void outOfScopeExceptionContainsTheFileWithTheException() throws Exception {
		given(aspect).hasClass("appns/App")
		.and(blade).classRequires("appns/bs/b1/Class1", "appns/App")
		.and(blade.workbench()).indexPageRequires("appns/bs/b1/Class1");
		when(blade.workbench()).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(OutOfBundleScopeRequirePathException.class, 
				"bs-bladeset/blades/b1/src/appns/bs/b1/Class1.js")
			.whereTopLevelExceptionIs(ContentProcessingException.class);
	}

	
	// ----------------------------------- C S S  -------------------------------------
 	@Test
 	public void aspectCssFilesAreBundledInTheWorkbench() throws Exception {
		given(aspect).containsFileWithContents("themes/standard/style.css", "ASPECT theme content")
			.and(aspect).hasClass("appns/Class1")
    		.and(workbench).indexPageRefersTo("appns.Class1");
 		when(workbench).requestReceivedInDev("css/standard/bundle.css", response);
 		then(response).containsText("ASPECT theme content");
 	}
	
 	@Test
 	public void aspectCssFilesAreBundledInTheWorkbenchEvenIfAspectSrcIsntUsed() throws Exception {
 		given(aspect).containsFileWithContents("themes/standard/style.css", "ASPECT theme content")
        	.and(workbench).hasClass("appns/workbench/Class1")
        	.and(workbench).indexPageRefersTo("appns.workbench.Class1");
 		when(workbench).requestReceivedInDev("css/standard/bundle.css", response);
 		then(response).containsText("ASPECT theme content");
 	}
 	
 	@Test
 	public void bladesetCssFilesAreBundledWhenReferencedInTheWorkbench() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClass("appns.bs.Class1")
			.and(bladeset).containsFileWithContents("themes/standard/style.css", "BLADESET theme content")
			.and(workbench).indexPageRefersTo("appns.bs.Class1");
		when(workbench).requestReceivedInDev("css/standard/bundle.css", response);
 		then(response).containsText("BLADESET theme content");
 	}
	 
 	@Test
 	public void bladeCssFilesAreBundledWhenReferencedInTheWorkbench() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).containsFileWithContents("themes/standard/style.css", "BLADE theme content")
			.and(workbench).indexPageRefersTo("appns.bs.b1.Class1");
		when(workbench).requestReceivedInDev("css/standard/bundle.css", response);
 		then(response).containsText("BLADE theme content");
 	}
	
	// TODO This was the previous behaviour for bladerunner - this will now be opt-in?
	@Ignore
	@Test
	public void sdkLibCssFilesAreNotBundledAsCommonCssInTheWorkbenchWhenNotReferenced() throws Exception {

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
	public void bladesCanNotDependOnWorkbenchClasses() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(workbench).hasClass("appns.WorkbenchClass")
			.and(blade).classDependsOn("appns.bs.b1.BladeClass", "appns.WorkbenchClass")
			.and(workbench).indexPageRefersTo("appns.bs.b1.BladeClass");
		when(workbench).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("appns.bs.b1.BladeClass =")
			.and(response).doesNotContainText("appns.WorkbenchClass =");
	}
}
