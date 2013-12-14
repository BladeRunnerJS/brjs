package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsBundlerContentPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'appns' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectAppThirdpartyLibBundling extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib appThirdparty, appThirdparty2;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();

			app = brjs.app("app1");
			aspect = app.aspect("default");
			
			appThirdparty = app.nonBladeRunnerLib("app-thirdparty");
			appThirdparty2 = app.nonBladeRunnerLib("app-thirdparty2");
	}
	
	@Test
	public void interLibraryDependenciesAppearAheadOfTheDependentLibrary() throws Exception {
		given(appThirdparty).containsFileWithContents("library.manifest", "js: src1.js\ndepends: app-thirdparty2")
			.and(appThirdparty).containsFile("src1.js")
			.and(appThirdparty2).containsFileWithContents("library.manifest", "js: src2.js")
			.and(appThirdparty2).containsFile("src2.js")
			.and(aspect).indexPageRefersTo(appThirdparty);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("// app-thirdparty2\nsrc2.js\n\n\n\n\n// app-thirdparty");
	}

	@Test
	public void aspectBundlesAppLegacyThirdpartyLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(appThirdparty).containsFileWithContents("library.manifest", "js: src1.js, src2.js")
			.and(appThirdparty).containsFiles("src1.js", "src2.js", "src3.js")
			.and(aspect).indexPageRefersTo(appThirdparty);
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("src1.js")
			.and(response).containsText("src2.js")
			.and(response).doesNotContainText("src3.js");
	}
	
	@Test
	public void aspectBundlesAppLegacyThirdpartyLibsIfTheyAreIncludedInAClass() throws Exception {
		given(appThirdparty).containsFileWithContents("library.manifest", "js: src.js")
			.and(appThirdparty).containsFile("src.js")
			.and(aspect).hasPackageStyle(NamespacedJsBundlerContentPlugin.JS_STYLE)
			.and(aspect).classRefersToThirdpartyLib("appns.Class1", appThirdparty)
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("src.js");
	}
	
	@Test
	public void aspectBundlesContainAppLegacyThirdpartyLibsIfTheyAreRequiredInAClass() throws Exception {
		given(appThirdparty).containsFileWithContents("library.manifest", "js: src.js")
			.and(appThirdparty).containsFile("src.js")
			.and(aspect).classRequires("appns.Class1", "app-thirdparty")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("src.js");
	}
}
