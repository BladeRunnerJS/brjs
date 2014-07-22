package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
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
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();

			app = brjs.app("app1");
			aspect = app.aspect("default");
			
			appThirdparty = app.jsLib("app-thirdparty");
			appThirdparty2 = app.jsLib("app-thirdparty2");
	}
	
	@Test
	public void interLibraryDependenciesAppearAheadOfTheDependentLibrary() throws Exception {
		given(appThirdparty).containsFileWithContents("thirdparty-lib.manifest", "js: src1.js\n"+"depends: app-thirdparty2\n"+"exports: appthirdparty")
			.and(appThirdparty).containsFile("src1.js")
			.and(appThirdparty2).containsFileWithContents("thirdparty-lib.manifest", "js: src2.js\n"+"exports: lib")
			.and(appThirdparty2).containsFile("src2.js")
			.and(aspect).indexPageRequires(appThirdparty);
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsOrderedTextFragments("// app-thirdparty2", "// app-thirdparty\n");
	}

	@Test
	public void aspectBundlesAppLegacyThirdpartyLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(appThirdparty).containsFileWithContents("thirdparty-lib.manifest", "js: src1.js, src2.js\n"+"exports: appThirdparty")
			.and(appThirdparty).containsFiles("src1.js", "src2.js", "src3.js")
			.and(aspect).indexPageRequires(appThirdparty);
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsOrderedTextFragments("src1.js", "src2.js")
			.and(response).doesNotContainText("src3.js");
	}
	
	@Test
	public void libraryFilesAreOrderedAsDefinedWithinTheManifest() throws Exception {
		given(appThirdparty).containsFileWithContents("thirdparty-lib.manifest", "js: src2.js, src1.js\n"+"exports: appThirdparty")
			.and(appThirdparty).containsFiles("src1.js", "src2.js", "src3.js")
			.and(aspect).indexPageRequires(appThirdparty);
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsOrderedTextFragments("src2.js", "src1.js")
			.and(response).doesNotContainText("src3.js");
	}
	
	@Test
	public void aspectBundlesAppLegacyThirdpartyLibsIfTheyAreIncludedInAClass() throws Exception {
		given(appThirdparty).containsFileWithContents("thirdparty-lib.manifest", "js: src.js\n"+"exports: appThirdparty")
			.and(appThirdparty).containsFile("src.js")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).classDependsOnThirdpartyLib("appns.Class1", appThirdparty)
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("src.js");
	}
	
	@Test
	public void aspectBundlesContainAppLegacyThirdpartyLibsIfTheyAreRequiredInAClass() throws Exception {
		given(appThirdparty).containsFileWithContents("thirdparty-lib.manifest", "js: src.js\n"+"exports: appThirdparty")
			.and(appThirdparty).containsFile("src.js")
			.and(aspect).classRequires("appns/Class1", "app-thirdparty")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsText("src.js");
	}
}
