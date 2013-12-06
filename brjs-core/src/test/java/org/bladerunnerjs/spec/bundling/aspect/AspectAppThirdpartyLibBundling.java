package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.core.plugin.bundler.js.NamespacedJsBundlerPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'mypkg' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectAppThirdpartyLibBundling extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib appLegacyThirdparty, appLegacyThirdparty2;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();

			app = brjs.app("app1");
			aspect = app.aspect("default");
			
			appLegacyThirdparty = app.nonBladeRunnerLib("app-legacy-thirdparty");
			appLegacyThirdparty = app.nonBladeRunnerLib("app-legacy-thirdparty2");
	}
	
	@Ignore
	@Test
	public void interLibraryDependenciesAppearAheadOfTheDependentLibrary() throws Exception {
		given(appLegacyThirdparty).hasClass("appThirdpartyLibName.Class1")
			.and(aspect).indexPageRefersTo(appLegacyThirdparty)
			.and(appLegacyThirdparty).containsFileWithContents("library.manifest", "js: lib1.js\ndepends: app-legacy-thirdparty2")
			.and(appLegacyThirdparty).containsFile("lib1.js")
			.and(appLegacyThirdparty2).containsFileWithContents("library.manifest", "js: lib2.js")
			.and(appLegacyThirdparty2).containsFile("lib2.js");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("// app-legacy-thirdparty2\n\n\n\n\n// app-legacy-thirdparty");
	}

	@Test
	public void aspectBundlesAppLegacyThirdpartyLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(appLegacyThirdparty).hasClasses("appThirdparty.Class1", "appThirdparty.Class2")
			.and(aspect).indexPageRefersTo("appThirdparty.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appThirdparty.Class1")
			.and(response).doesNotContainClasses("appThirdparty.Class2");
	}
	
	@Test
	public void aspectBundlesAppLegacyThirdpartyLibsIfTheyAreReferencedInAClass() throws Exception {
		given(appLegacyThirdparty).hasBeenCreated()
			.and(appLegacyThirdparty).hasPackageStyle("appThirdpartyLibName", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(appLegacyThirdparty).hasClass("appThirdparty.Class1")
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasPackageStyle("src/mypkg", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "appThirdparty.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appThirdparty.Class1");
	}
	
	@Test
	public void aspectBundlesContainAppLegacyThirdpartyLibsIfTheyAreRequiredInAClass() throws Exception {
		given(appLegacyThirdparty).hasClass("appThirdpartyLibName.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRequires("mypkg.Class1", "appThirdpartyLibName.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appThirdpartyLibName.Class1");
	}
}
