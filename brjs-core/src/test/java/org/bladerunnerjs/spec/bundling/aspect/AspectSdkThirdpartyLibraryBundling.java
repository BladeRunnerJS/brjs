package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'mypkg' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectSdkThirdpartyLibraryBundling extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private JsLib sdkLegacyThirdparty, sdkLegacyThirdparty2;
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
			
			sdkLegacyThirdparty = brjs.sdkNonBladeRunnerLib("legacy-thirdparty");
			sdkLegacyThirdparty2 = brjs.sdkNonBladeRunnerLib("legacy-thirdparty2");
	}

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
