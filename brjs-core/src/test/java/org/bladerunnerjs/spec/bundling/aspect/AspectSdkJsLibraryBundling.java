package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.core.plugin.bundler.js.NamespacedJsBundlerPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'mypkg' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectSdkJsLibraryBundling extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private JsLib sdkJsLib, sdkLegacyThirdparty, sdkLegacyThirdparty2;
	private StringBuffer response = new StringBuffer();
	
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
			
			sdkJsLib = brjs.sdkLib();
			sdkLegacyThirdparty = brjs.sdkNonBladeRunnerLib("legacy-thirdparty");
			sdkLegacyThirdparty2 = brjs.sdkNonBladeRunnerLib("legacy-thirdparty2");
			
	}

	// ---------------------------- S D K   J S   L I B S ----------------------------------
	// AspectSdkJsLibraryBundling
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(sdkJsLib).hasClass("sdkJsLib.Class1")
			.and(aspect).indexPageRefersTo("sdkJsLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("sdkJsLib.Class1");
	}
	
	@Ignore //TODO: fix this, how do we distinguish between different types of JsLibs so the bundler plugins know which to create files for?
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreReferencedInAClass() throws Exception {
		given(sdkJsLib).hasBeenCreated()
			.and(sdkJsLib).hasPackageStyle("src/sdkJsLib", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(sdkJsLib).hasClass("sdkJsLib.Class1")
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasPackageStyle("src/mypkg", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "sdkJsLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("mypkg.Class1", "sdkJsLib.Class1")
			.and(response).doesNotContainText("require");
	}
	
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreRequiredInAClass() throws Exception {
		given(sdkJsLib).hasClass("sdkJsLib.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRequires("mypkg.Class1", "sdkJsLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("sdkJsLib.Class1");
	}
	
	
	// --------------------- S D K   T H I R D P A R T Y   L I B S --------------------------
	// AspectSdkThirdpartyLibraryBundling
	@Test
	public void aspectBundlesContainLegacyThirdpartyLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends:")
			.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.lib = { }")
			.and(aspect).indexPageRefersTo(sdkLegacyThirdparty);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void aspectBundlesContainLegacyThirdpartyLibsIfTheyAreReferencedInAnAspectClass() throws Exception {		
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends:")
    		.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.lib = { }")
    		.and(aspect).hasClass("mypkg.Class1")
    		.and(aspect).classRequiresThirdpartyLib("mypkg.Class1", sdkLegacyThirdparty)
    		.and(aspect).indexPageRefersTo("mypkg.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void canBundleLegacyThirdpartyLibsIfTheyAreReferencedInABladeset() throws Exception {		
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends:")
    		.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.lib = { }")
    		.and(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
    		.and(bladeset).classRequiresThirdpartyLib("mypkg.bs.Class1", sdkLegacyThirdparty)
    		.and(aspect).indexPageRefersTo("mypkg.bs.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void canBundleLegacyThirdpartyLibsIfTheyAreReferencedInABlade() throws Exception {		
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends:")
    		.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.lib = { }")
    		.and(blade).hasClasses("mypkg.bs.b1.Class1", "mypkg.bs.b1.Class2")
    		.and(blade).classRequiresThirdpartyLib("mypkg.bs.b1.Class1", sdkLegacyThirdparty)
    		.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.lib = { }");
	}
	
	@Test
	public void canBundleLegacyThirdpartyLibAndItsOtherDependencyLibsIfTheyAreReferencedInABlade() throws Exception {		
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty2).hasBeenCreated()
			.and(sdkLegacyThirdparty2).containsFileWithContents("library.manifest", "depends: ")
			.and(sdkLegacyThirdparty2).containsFileWithContents("src.js", "window.legacy2 = { }")
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends: legacy-thirdparty2")
			.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.legacy = { }")
			.and(aspect).indexPageRefersTo(sdkLegacyThirdparty);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.legacy = { }")
			.and(response).containsText("window.legacy2 = { }");
	}
	
	@Ignore // TODO understand why this throws an UnknownFormatConversion Exception
	@Test
	public void anExceptionIsThrownIfThirdpartyLibHasDependencyOnAFileThatDoesNotExist() throws Exception {
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends: blabla")
			.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.lib = { }")
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).indexPageRefersTo(sdkLegacyThirdparty);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void legacyThirdpartyLibWhichIsASubstringOfAnotherLibDoesNotGetBundledWhenReferencedInAspectIndexPage() throws Exception {		
		given(sdkLegacyThirdparty).hasBeenCreated()
			.and(sdkLegacyThirdparty).containsFileWithContents("library.manifest", "depends:\n")
			.and(sdkLegacyThirdparty).containsFileWithContents("src.js", "window.legacy = { }")
			.and(sdkLegacyThirdparty2).hasBeenCreated()
			.and(sdkLegacyThirdparty2).containsFileWithContents("library.manifest", "depends:\n")
			.and(sdkLegacyThirdparty2).containsFileWithContents("src.js", "window.legacy2 = { }")
			.and(aspect).indexPageRefersTo(sdkLegacyThirdparty2);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("window.legacy2 = { }")
			.and(response).doesNotContainText("window.legacy = { }");
	}
}
