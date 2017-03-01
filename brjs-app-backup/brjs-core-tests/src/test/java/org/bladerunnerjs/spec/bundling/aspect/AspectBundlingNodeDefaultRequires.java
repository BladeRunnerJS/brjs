package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectBundlingNodeDefaultRequires extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib sdkNamespaceLib, sdkCommonJsLib;
	private StringBuffer response = new StringBuffer();

	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();

		app = brjs.app("app1");
		aspect = app.aspect("default");

		sdkNamespaceLib = brjs.sdkLib("sdkNamespaceLib");
		sdkCommonJsLib = brjs.sdkLib("sdkCommonJsLib");

		given(sdkNamespaceLib).hasNamespacedJsPackageStyle()
			.and(sdkCommonJsLib).hasCommonJsPackageStyle();
	}

	// index.js references: common-js
	@Test
	public void anIndexJsFileContainedAspectSrcRootIsBundledWhenRequiredWithoutTheAssetNameCommonJsSimple() throws Exception {
		given(aspect).hasClass("index")
			.and(aspect).indexPageRequires("appns");

		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("appns");
	}

	@Test
	public void anIndexJsFileContainedInAnSdkLibraryRootIsBundledWhenRequiredWithoutTheAssetNameCommonJsSimple() throws Exception {
		given(sdkCommonJsLib).containsFileWithContents("br-lib.conf", "requirePrefix: root/pkg")
			.and(sdkCommonJsLib).hasClass("root/pkg/index")
			.and(aspect).indexPageRequires("root/pkg");

		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("root/pkg");
	}

	@Test
	public void anIndexJsFileContainedInAnSdkLibraryIsBundledWhenRequiredWithoutTheAssetNameCommonJsSimple() throws Exception {
		given(sdkCommonJsLib).hasClass("pkg1/index")
			.and(aspect).indexPageRequires("sdkCommonJsLib/pkg1");

		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("sdkCommonJsLib/pkg1/index");
	}

	@Test
	public void anIndexJsFileContainedInAnSdkLibraryIsBundledWhenRequiredWithoutTheAssetNameCommonJs() throws Exception {
		given(sdkCommonJsLib).hasClass("pkg1/pkg2/Class1")
			.and(sdkCommonJsLib).hasClass("pkg1/index")
			.and(aspect).indexPageRequires("sdkCommonJsLib/pkg1", "sdkCommonJsLib/pkg1/pkg2/Class1");

		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("sdkCommonJsLib/pkg1/index");
	}

	// index.js references: namespaced-js
	@Test
	public void anIndexJsFileContainedInAnSdkLibraryIsBundledWhenRequiredWithoutTheAssetNameNamepacedJsSimple() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(sdkNamespaceLib).hasClass("sdkNamespaceLib.pkg1.index")
			.and(aspect).indexPageRefersTo("sdkNamespaceLib.pkg1");

		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsNamespacedJsClasses("sdkNamespaceLib.pkg1.index");
	}

	@Test
	public void anIndexJsFileContainedInAnSdkLibraryIsBundledWhenRequiredWithoutTheAssetNameNamepacedJs() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(sdkNamespaceLib).hasClass("sdkNamespaceLib.pkg1.pkg2.Class1")
			.and(sdkNamespaceLib).hasClass("sdkNamespaceLib.pkg1.index")
			.and(aspect).indexPageRefersTo("sdkNamespaceLib.pkg1", "sdkNamespaceLib.pkg1.pkg2.Class1");

		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsNamespacedJsClasses("sdkNamespaceLib.pkg1.index");
	}

	// index.js references: mixed common-js lib, namespaced-js aspect
	@Test
	public void anIndexJsFileContainedInAnSdkLibraryIsBundledWhenRequiredWithoutTheAssetNameNamepacedJsAspectCommonJsLib() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(sdkCommonJsLib).hasClass("pkg1/pkg2/Class1")
			.and(sdkCommonJsLib).hasClass("pkg1/index")
			.and(aspect).indexPageRefersTo("sdkCommonJsLib.pkg1", "sdkCommonJsLib.pkg1.pkg2.Class1");

		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("sdkCommonJsLib/pkg1");
	}

	@Test
	public void anIndexJsFileContainedInAnSdkLibraryIsBundledWhenReferredToWithoutTheAssetNameNamepacedJsAspectCommonJsLibNoNamespaceEnforcement_HACK() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(sdkCommonJsLib).containsFile("no-namespace-enforcement")
			.and(sdkCommonJsLib).hasClass("libRootName/pkg1/pkg2/Class1")
			.and(sdkCommonJsLib).hasClass("libRootName/pkg1/index")
			.and(aspect).indexPageRefersTo("libRootName.pkg1", "libRootName.pkg1.pkg2.Class1");

		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("libRootName/pkg1");
	}

	@Test
	public void anIndexJsFileContainedInAnSdkLibraryIsBundledWhenRequiredWithoutTheAssetNameNamepacedJsAspectCommonJsLibNoNamespaceEnforcement_HACK() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(sdkCommonJsLib).containsFile("no-namespace-enforcement")
			.and(sdkCommonJsLib).hasClass("libRootName/pkg1/pkg2/Class1")
			.and(sdkCommonJsLib).hasClass("libRootName/pkg1/index")
			.and(aspect).indexPageRequires("libRootName/pkg1", "libRootName/pkg1/pkg2/Class1");

		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("libRootName/pkg1", "libRootName/pkg1/pkg2/Class1");
	}

}
