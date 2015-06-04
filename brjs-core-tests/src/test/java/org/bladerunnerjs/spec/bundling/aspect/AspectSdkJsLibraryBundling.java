package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


//TODO: why don't we get a namespace exception when we define classes outside of the namespace (e.g. 'appns' when the default namespace is 'appns')?
//TODO: we should fail-fast if somebody uses unquoted() in a logging assertion as it is only meant for exceptions where we can't easily ascertain the parameters
public class AspectSdkJsLibraryBundling extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib sdkLib;
	private StringBuffer response = new StringBuffer();
	private TestPack sdkLibTestPack;
	private JsLib sdkLib1;
	private JsLib sdkLib2;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			sdkLib = brjs.sdkLib("br");
			sdkLibTestPack = sdkLib.testType("unit").testTech("techy");
			sdkLib1 = brjs.sdkLib("lib1");
			sdkLib2 = brjs.sdkLib("lib2");
	}

	@Test
	public void aspectBundlesContainsNodeStyleSdkLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(sdkLib).hasCommonJsPackageStyle()
			.and(sdkLib).hasClass("br/SdkClass")
			.and(aspect).indexPageHasContent("require('br/SdkClass');");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("br/SdkClass");
	}
	
	@Test
	public void aspectBundlesContainsNamespaceStyleSdkLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).hasClass("br.SdkClass")
			.and(aspect).indexPageHasContent("require('br/SdkClass');");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("br/SdkClass");
	}
	
	
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreReferencedInAClass() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.AspectClass")
			.and(sdkLib).hasClass("br/SdkClass")
			.and(aspect).indexPageRefersTo("appns.AspectClass")
			.and(aspect).classDependsOn("appns.AspectClass", "br.SdkClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("br.SdkClass");
	}
	
	@Test
	public void aspectBundlesContainSdkLibsIfTheyAreRequiredInAClass() throws Exception {
		given(aspect).hasClass("appns/AspectClass")
			.and(sdkLib).hasClass("br/SdkClass")
			.and(aspect).indexPageRefersTo("appns.AspectClass")
			.and(aspect).classRequires("appns/AspectClass", "br/SdkClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("br.SdkClass");
	}
	
	@Test
	public void weCanGenerateABundleForJsLibTestPacks() throws Exception {
		given(sdkLib).hasClass("br/SdkClass")
			.and(sdkLibTestPack).testRequires("test.js", "br/SdkClass");
		when(sdkLibTestPack).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("br/SdkClass");
	}
	
	@Test
	public void noNamespaceEnforcementFlagDoesntCauseConflictBetweenLibraryPrefixesWhenPackageDirectoryIsUsed() throws Exception {
		given(sdkLib1).hasBeenCreated()
			.and(sdkLib1).containsFileWithContents("br-lib.conf","requirePrefix: sdk/lib1")
			.and(sdkLib1).containsFile("no-namespace-enforcement")
			.and(sdkLib1).hasClass("sdk/lib1/Lib1Class")
			.and(sdkLib2).hasBeenCreated()
			.and(sdkLib2).containsFileWithContents("br-lib.conf","requirePrefix: sdk/lib2")
			.and(sdkLib2).containsFile("no-namespace-enforcement")
			.and(sdkLib2).hasClass("sdk/lib2/Lib2Class")
			.and(aspect).indexPageHasContent("getLogger('sdk')");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void noNamespaceEnforcementFlagDoesntCauseConflictBetweenRootLibraryPrefixesWhenPackageDirectoryIsUsed() throws Exception {
		given(sdkLib1).hasBeenCreated()
			.and(sdkLib1).containsFileWithContents("br-lib.conf","requirePrefix: sdk")
			.and(sdkLib1).containsFile("no-namespace-enforcement")
			.and(sdkLib1).hasClass("sdk/lib1/Lib1Class")
			.and(sdkLib2).hasBeenCreated()
			.and(sdkLib2).containsFileWithContents("br-lib.conf","requirePrefix: sdk/lib2")
			.and(sdkLib2).containsFile("no-namespace-enforcement")
			.and(sdkLib2).hasClass("sdk/lib2/Lib2Class")
			.and(aspect).indexPageHasContent("getLogger('sdk')");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
}
