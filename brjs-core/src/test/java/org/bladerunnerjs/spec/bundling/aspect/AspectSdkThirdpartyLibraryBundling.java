package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'appns' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectSdkThirdpartyLibraryBundling extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private JsLib thirdpartyLib, thirdpartyLib2, bootstrapLib, secondBootstrapLib;
	private StringBuffer response = new StringBuffer();
	private JsLib thirdBootstrapLib;
	
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
			thirdpartyLib = brjs.sdkNonBladeRunnerLib("thirdparty-lib1");
			thirdpartyLib2 = brjs.sdkNonBladeRunnerLib("thirdparty-lib2");
			bootstrapLib = brjs.sdkNonBladeRunnerLib("br-bootstrap");
			secondBootstrapLib = brjs.sdkNonBladeRunnerLib("secondBootstrapLib");
			thirdBootstrapLib = brjs.sdkNonBladeRunnerLib("thirdBootstrapLib");
	}
	
	// Bootstrap tests --
	@Test
	public void weBundleBootstrapIfItExists() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(bootstrapLib).hasBeenCreated()
			.and(bootstrapLib).containsFileWithContents("library.manifest", "js: bootstrap.js\n"+"exports: lib")
			.and(bootstrapLib).containsFileWithContents("bootstrap.js", "// this is bootstrap");
		when(app).requestReceived("/default-aspect/thirdparty/bundle.js", response);
		then(response).containsText("// br-bootstrap");
		then(response).containsText("// this is bootstrap"); 
	}
	
	@Test
	public void weBundleBootstrapFirst() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(bootstrapLib).hasBeenCreated()
			.and(bootstrapLib).containsFileWithContents("library.manifest", "js: bootstrap.js\n"+"exports: lib")
			.and(bootstrapLib).containsFileWithContents("bootstrap.js", "// this is bootstrap");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsOrderedTextFragments(
				"// br-bootstrap",
				"// this is bootstrap",
				"define('appns/Class1'" ); 
	}
	
	@Test
	public void weBundleBootstrapSrcInASubDir() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(bootstrapLib).hasBeenCreated()
			.and(bootstrapLib).containsFileWithContents("library.manifest", "js: sub/dir/bootstrap.js\n"+"exports: lib")
			.and(bootstrapLib).containsFileWithContents("sub/dir/bootstrap.js", "// this is bootstrap");
		when(app).requestReceived("/default-aspect/thirdparty/bundle.js", response);
		then(response).containsText("// br-bootstrap");
		then(response).containsText("// this is bootstrap"); 
	}
	
	@Test
	public void bootstrapCanDependOnAnotherLibraryWhichIsBundledBeforeTheRestOfTheBundle() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(bootstrapLib).hasBeenCreated()
			.and(bootstrapLib).containsFileWithContents("library.manifest", "depends: secondBootstrapLib\n"+"exports: lib")
			.and(secondBootstrapLib).hasBeenCreated()
			.and(secondBootstrapLib).containsFileWithContents("library.manifest", "js: someFile.js\n"+"exports: lib")
			.and(secondBootstrapLib).containsFileWithContents("someFile.js", "// this is secondBootstrapLib");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsOrderedTextFragments(
				"// secondBootstrapLib",
				"// br-bootstrap",
				"appns.Class1" ); 
	}
	
	@Test
	public void bootstrapCanHaveADependencyChain3LevelsDeep() throws Exception {
		given(aspect).hasClass("appns.Class1")
		.and(aspect).indexPageRefersTo("appns.Class1")
		.and(bootstrapLib).hasBeenCreated()
		.and(bootstrapLib).containsFileWithContents("library.manifest", "depends: secondBootstrapLib\n"+"exports: lib")
		.and(secondBootstrapLib).hasBeenCreated()
		.and(secondBootstrapLib).containsFileWithContents("library.manifest", "js: someFile.js\n"+"exports: lib\n"+"depends: thirdBootstrapLib")
		.and(secondBootstrapLib).containsFileWithContents("someFile.js", "// this is secondBootstrapLib")
		.and(thirdBootstrapLib).hasBeenCreated()
		.and(thirdBootstrapLib).containsFileWithContents("library.manifest", "js: someFile.js\n"+"exports: lib")
		.and(thirdBootstrapLib).containsFileWithContents("someFile.js", "// this is thirdBootstrapLib");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsOrderedTextFragments(
				"// thirdBootstrapLib",
				"// secondBootstrapLib",
				"// br-bootstrap",
				"appns.Class1" ); 
	}
	
	@Test
	public void bootstrapAndItsDependenciesAppearBeforeAllOtherAspectDependencies() throws Exception {
		given(aspect).hasClass("appns.Class1")
    		.and(aspect).indexPageRefersTo("appns.Class1", "thirdparty-lib1")
    		.and(bootstrapLib).hasBeenCreated()
    		.and(bootstrapLib).containsFileWithContents("library.manifest", "depends: secondBootstrapLib\n"+"exports: lib")
    		.and(secondBootstrapLib).hasBeenCreated()
    		.and(secondBootstrapLib).containsFileWithContents("library.manifest", "js: someFile.js\n"+"exports: lib\n"+"depends: thirdBootstrapLib")
    		.and(secondBootstrapLib).containsFileWithContents("someFile.js", "// this is secondBootstrapLib")
    		.and(thirdpartyLib).containsFileWithContents("library.manifest", "exports: lib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.lib = { }");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsOrderedTextFragments(
				"// secondBootstrapLib",
				"// br-bootstrap",
				"// thirdparty-lib1",
				"appns.Class1" ); 
	}
	
	@Test
	public void weDontBundleBootstrapIfThereIsNoSourceToBundle() throws Exception {
		given(aspect).indexPageHasContent("the page")
			.and(bootstrapLib).hasBeenCreated()
			.and(bootstrapLib).containsFileWithContents("library.manifest", "js: bootstrap.js\n"+"exports: lib")
			.and(bootstrapLib).containsFileWithContents("bootstrap.js", "// this is bootstrap");
		when(app).requestReceived("/default-aspect/thirdparty/bundle.js", response);
		then(response).isEmpty();
	}
	
	@Test  // ignore circular dependencies originating from bootstrap since its our library that doesnt matter if it has a circular dependency
	public void circularDependenciesOriginatingFromBootstrapAreSilentlyIgnored() throws Exception {
		given(aspect).hasClass("appns.Class1")
    		.and(aspect).indexPageRefersTo("appns.Class1", "thirdparty-lib1")
    		.and(bootstrapLib).hasBeenCreated()
    		.and(bootstrapLib).containsFileWithContents("library.manifest", "depends: secondBootstrapLib\n"+"exports: lib")
    		.and(secondBootstrapLib).hasBeenCreated()
    		.and(secondBootstrapLib).containsFileWithContents("library.manifest", "js: someFile.js\n"+"exports: lib\n"+"depends: br-bootstrap")
    		.and(secondBootstrapLib).containsFileWithContents("someFile.js", "// this is secondBootstrapLib")
    		.and(thirdpartyLib).containsFileWithContents("library.manifest", "exports: lib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.lib = { }");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsOrderedTextFragments(
				"// secondBootstrapLib",
				"// br-bootstrap",
				"// thirdparty-lib1",
				"appns.Class1" ); 
	}
	
	// Bootstrap tests end --

	@Test
	public void aspectBundlesContainLegacyThirdpartyLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "exports: lib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.lib = { }")
			.and(aspect).indexPageRequires(thirdpartyLib);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void aspectBundlesContainLegacyThirdpartyLibsIfTheyAreReferencedInAnAspectClass() throws Exception {		
		given(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "exports: lib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.lib = { }")
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).classRequiresThirdpartyLib("appns.Class1", thirdpartyLib)
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void canBundleLegacyThirdpartyLibsIfTheyAreReferencedInABladeset() throws Exception {		
		given(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "exports: lib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.lib = { }")
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classRequiresThirdpartyLib("appns.bs.Class1", thirdpartyLib)
			.and(aspect).indexPageRefersTo("appns.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void canBundleLegacyThirdpartyLibsIfTheyAreReferencedInABlade() throws Exception {		
		given(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "exports: lib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.lib = { }")
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classRequiresThirdpartyLib("appns.bs.b1.Class1", thirdpartyLib)
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void canBundleLegacyThirdpartyLibAndItsOtherDependencyLibsIfTheyAreReferencedInABlade() throws Exception {		
		given(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib2).hasBeenCreated()
			.and(thirdpartyLib2).containsFileWithContents("library.manifest", "exports: lib")
			.and(thirdpartyLib2).containsFileWithContents("src.js", "window.legacy2 = { }")
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "depends: thirdparty-lib2\n"+"exports: lib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.legacy = { }")
			.and(aspect).indexPageRequires(thirdpartyLib);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.legacy = { }")
			.and(response).containsText("window.legacy2 = { }");
	}
	
	@Test
	public void anExceptionIsThrownIfThirdpartyLibHasDependencyOnAFileThatDoesNotExist() throws Exception {
		given(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "depends: blabla\n"+"exports: lib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.lib = { }")
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRequires(thirdpartyLib);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void legacyThirdpartyLibWhichIsASubstringOfAnotherLibDoesNotGetBundledWhenReferencedInAspectIndexPage() throws Exception {		
		given(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "exports: lib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.legacy = { }")
			.and(thirdpartyLib2).hasBeenCreated()
			.and(thirdpartyLib2).containsFileWithContents("library.manifest", "exports: lib")
			.and(thirdpartyLib2).containsFileWithContents("src.js", "window.legacy2 = { }")
			.and(aspect).indexPageRequires(thirdpartyLib2);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.legacy2 = { }")
			.and(response).doesNotContainText("window.legacy = { }");
	}
	
	@Test
	public void thirdpartyLibWithHtmlDoesNotGetScannedForDependenciesInAspectIndexPage() throws Exception {
		given(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("library.manifest", "exports: lib")
			.and(thirdpartyLib).containsFileWithContents("src.js", "window.legacy = { }")
			.and(thirdpartyLib).containsFileWithContents("html/doNotScan.html", "require('thirdparty-lib2');")
			.and(thirdpartyLib2).hasBeenCreated()
			.and(thirdpartyLib2).containsFileWithContents("library.manifest", "exports: lib")
			.and(thirdpartyLib2).containsFileWithContents("src.js", "window.legacy2 = { }")
			.and(aspect).indexPageRequires(thirdpartyLib);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.legacy = { }")
			.and(response).doesNotContainText("window.legacy2 = { }");
	}
	
	@Test
	public void librariesAreWrappedInADefineBlock() throws Exception {
		given(thirdpartyLib).hasBeenCreated()
    		.and(thirdpartyLib).containsFileWithContents("library.manifest", "exports: thirdpartyLib")
    		.and(thirdpartyLib).containsFileWithContents("src.js", "window.thirdpartyLib = { }")
    		.and(aspect).indexPageRequires(thirdpartyLib);
    	when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
    	then(response).containsOrderedTextFragments("define('thirdparty-lib1', function(require, exports, module) {\n",
    						"module.exports = thirdpartyLib",
    					"});");
	}

	
	//TODO: remove this test when we have a NodeJS library plugin that reads the Package.json - also remove the check in ThirdpartySourceModule
	@Test
	public void librariesAreNotWrappedIfPackageJsonExistsr() throws Exception {
		given(thirdpartyLib).hasBeenCreated()
    		.and(thirdpartyLib).containsFileWithContents("library.manifest", "exports: someLib")
    		.and(thirdpartyLib).containsFileWithContents("package.json", "// some packagey stuff")
    		.and(thirdpartyLib).containsFileWithContents("src.js", "window.thirdpartyLib = { }")
    		.and(aspect).indexPageRequires(thirdpartyLib);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("define('thirdparty-lib1', function(require, exports, module) {\n");
	}
}
