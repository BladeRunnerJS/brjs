package org.bladerunnerjs.spec.bundling.testpack;

import static org.junit.Assert.assertEquals;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.SdkJsLib;
import org.junit.Before;
import org.junit.Test;


public class TestPackBundlingTest extends SpecTest
{
	private App app;
	private Aspect aspect;
	private TestPack aspectUTs, aspectATs;
	private StringBuffer response = new StringBuffer();
	private TestPack implicitDefaultAspectUTs;
	private SdkJsLib sdkLib;
	private JsLib appLib;
	private TestPack sdkLibUTs;
	private Bladeset defaultBladeset;
	private Blade bladeInDefaultBladeset;
	private Aspect defaultAspect;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			aspectUTs = aspect.testType("unit").testTech("TEST_TECH");
			aspectATs = aspect.testType("acceptance").testTech("TEST_TECH");
			defaultBladeset = app.defaultBladeset();
			bladeInDefaultBladeset = defaultBladeset.blade("b1");
			implicitDefaultAspectUTs = aspect.testType("unit").defaultTestTech();
			sdkLib = brjs.sdkLib("lib");
			sdkLibUTs = sdkLib.testType("unit").testTech("tech");
			appLib = app.jsLib("lib");
			defaultAspect = app.defaultAspect();
	}
	
	@Test
	public void twoTestPacksCanHaveTestsWithTheSameRequirePath() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspectUTs).hasClass("appns.appTestUtils.Class1")
			.and(aspectATs).hasClass("appns.appTestUtils.Class1")
			.and(aspectUTs).testRefersTo("pkg/test.js", "appns.appTestUtils.Class1");
		then(aspectUTs).bundledFilesEquals(
				aspectUTs.testSource().file("appns/appTestUtils/Class1.js"));
	}
	
	@Test
	public void srcTestCanLiveAtTechnologyLevel() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasTestClass("appns.Class1")
			.and(aspectUTs).testRefersTo("pkg/test.js", "appns.Class1");
		then(aspectUTs).bundledFilesEquals(
				aspect.assetLocation("src-test").file("appns/Class1.js"));
	}
	
	@Test
	public void srcTestCanLiveAtTestsAndTechnologyLevel() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspectUTs).hasClass("appns.aspectUT.Class1")
			.and(aspect).hasTestClass("appns.Class1")
			.and(aspectUTs).testRefersTo("pkg/test.js", "appns.aspectUT.Class1")
			.and(aspectUTs).classDependsOn("appns.aspectUT.Class1", "appns.Class1");
		then(aspectUTs).bundledFilesEquals(
				aspectUTs.testSource().file("appns/aspectUT/Class1.js"),
				aspect.assetLocation("src-test").file("appns/Class1.js"));
	}
	
	@Test
	public void testCodeCanUseRequires() throws Exception {
		given(aspect).hasCommonJsPackageStyle()
    		.and(aspect).hasClasses("appns/Class1")
    		.and(aspectUTs).testRequires("pkg/test.js", "appns/Class1");
    	then(aspectUTs).bundledFilesEquals(aspect.assetLocation("src").file("appns/Class1.js"));
	}
	
	@Test
	public void aspectTestsDirectoryCanStillBeUsed() throws Exception {
		// we cant use the node instances before this point since the test nodes depend on the 'tests' directory being present when they are instantiated
		given(brjs).containsFileWithContents("apps/app/default-aspect/tests/test-type/tech/tests/myTest.js", "require('appns/Class1');")
			.and( brjs.app("app").aspect("default") ).hasClass("appns/Class1");
		when( brjs.app("app").aspect("default").testType("type").testTech("tech") ).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("Class1");
	}
	
	@Test
	public void bladesetTestsDirectoryCanStillBeUsed() throws Exception {
		// we cant use the node instances before this point since the test nodes depend on the 'tests' directory being present when they are instantiated
		given(brjs).containsFileWithContents("apps/app/bs-bladeset/tests/test-type/tech/tests/myTest.js", "require('appns/bs/Class1');")
			.and( brjs.app("app").bladeset("bs") ).hasClass("appns/bs/Class1");
		when( brjs.app("app").bladeset("bs").testType("type").testTech("tech") ).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns/bs/Class1");
	}
	
	@Test
	public void bladeTestsDirectoryCanStillBeUsed() throws Exception {
		// we cant use the node instances before this point since the test nodes depend on the 'tests' directory being present when they are instantiated
		given(brjs).containsFileWithContents("apps/app/bs-bladeset/blades/b1/tests/test-type/tech/tests/myTest.js", "require('appns/bs/b1/Class1');")
			.and( brjs.app("app").bladeset("bs").blade("b1") ).hasClass("appns/bs/b1/Class1");
		when( brjs.app("app").bladeset("bs").blade("b1").testType("type").testTech("tech") ).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns/bs/b1/Class1");
	}
	
	@Test
	public void workbenchTestsDirectoryCanStillBeUsed() throws Exception {
		// we cant use the node instances before this point since the test nodes depend on the 'tests' directory being present when they are instantiated
		given( brjs ).containsFileWithContents("apps/app/bs-bladeset/blades/b1/workbench/tests/test-type/tech/tests/myTest.js", "require('appns/Class1');")
			.and( brjs.app("app").bladeset("bs").blade("b1").workbench() ).hasClass("Class1");
		when( brjs.app("app").bladeset("bs").blade("b1").workbench().testType("type").testTech("tech") ).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns/Class1");
	}
	
	@Test
	public void testTechDirIsOptional() throws Exception {
		given(aspect).hasClasses("appns/Class1")
    		.and(implicitDefaultAspectUTs).testRequires("test.js", "appns/Class1");
		when( implicitDefaultAspectUTs ).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(implicitDefaultAspectUTs).bundledFilesEquals(aspect.assetLocation("src").file("appns/Class1.js"))
    		.and(aspect).hasFile("test-unit/tests/test.js");
	}
	
	@Test
	public void testTechDirIsOptionalInSdkLibs() throws Exception {
		given(sdkLib).hasClasses("lib/Class1")
			.and( sdkLib.testType("unit").defaultTestTech() ).testRequires("test.js", "lib/Class1");
		when( sdkLib.testType("unit").defaultTestTech() ).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then( sdkLib.testType("unit").defaultTestTech() ).bundledFilesEquals(sdkLib.assetLocation("src").file("lib/Class1.js"))
			.and(response).containsNamespacedJsClasses("Class1")
			.and( sdkLib ).hasFile("test-unit/tests/test.js");
	}
	
	@Test
	public void testTechDirIsOptionalInAppLibs() throws Exception {
		given(appLib).hasClasses("lib/Class1")
			.and( appLib.testType("unit").defaultTestTech() ).testRequires("test.js", "lib/Class1");
		when( appLib.testType("unit").defaultTestTech() ).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then( appLib.testType("unit").defaultTestTech() ).bundledFilesEquals(appLib.assetLocation("src").file("lib/Class1.js"))
			.and(response).containsNamespacedJsClasses("Class1")
			.and( appLib ).hasFile("test-unit/tests/test.js");
	}
	
	@Test
	public void impliedRequirePrefixIsNotUsedInLibrarySrcTestDirectoryIfNoNamespaceEnforcement_HACK_FlagIsUsed() throws Exception {
		given(sdkLib).containsFile("no-namespace-enforcement")
			.and(sdkLibUTs).hasTestClass("pkg1/pkg2/pkg3/SomeClass")
			.and(sdkLibUTs).testRequires("SomeTest.js", "pkg1/pkg2/pkg3/SomeClass");
    	when(sdkLibUTs).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).containsCommonJsClasses("pkg1/pkg2/pkg3/SomeClass");
	}
	
	@Test
	public void bundleCanBeGeneratedForABladeInADefaultBladeset() throws Exception {
		given(bladeInDefaultBladeset).hasClasses("Class1")
    		.and( bladeInDefaultBladeset.testType("unit").defaultTestTech() ).testRequires("test.js", "appns/b1/Class1");
		when( bladeInDefaultBladeset.testType("unit").defaultTestTech() ).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then( bladeInDefaultBladeset.testType("unit").defaultTestTech() ).bundledFilesEquals(bladeInDefaultBladeset.assetLocation("src").file("Class1.js"))
    		.and(response).containsCommonJsClasses("appns/b1/Class1");
	}
	
	@Test
	public void bundleCanBeGeneratedForTestsInsideADefaultAspect() throws Exception {
		given(defaultAspect).hasClasses("App")
    		.and( defaultAspect.testType("unit").defaultTestTech() ).testRequires("test.js", "appns/App");
		when( defaultAspect.testType("unit").defaultTestTech() ).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then( defaultAspect.testType("unit").defaultTestTech() ).bundledFilesEquals(defaultAspect.assetLocation("src").file("App.js"))
    		.and(response).containsCommonJsClasses("appns/App");
	}
	
	@Test
	public void bundleCanBeGeneratedForTestsInsideADefaultAspectWhereABladeInTheDefaultBladesetIsAlsoUsed() throws Exception {
		given(defaultAspect).hasClasses("App")
    		.and( defaultAspect.testType("unit").defaultTestTech() ).testRequires("test.js", "appns/App")
    		.and(bladeInDefaultBladeset).hasBeenCreated();
		when( defaultAspect.testType("unit").defaultTestTech() ).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then( defaultAspect.testType("unit").defaultTestTech() ).bundledFilesEquals(defaultAspect.assetLocation("src").file("App.js"))
    		.and(response).containsCommonJsClasses("appns/App");
	}
	
	@Test // test when an existing app is used and the model re-created the 'discoverAllChildren()' method doesnt discover test packs as children of the default bladeset instead of the default aspect
	public void bundleCanBeGeneratedForTestsInsideADefaultAspectWhereABladeInTheDefaultBladesetIsAlsoUsedAndANewModelIsUsed() throws Exception {
		given(defaultAspect).hasClasses("App")
    		.and( defaultAspect.testType("unit").defaultTestTech() ).testRequires("test.js", "appns/App")
    		.and(bladeInDefaultBladeset).hasBeenCreated()
    		.and(brjs).hasBeenAuthenticallyReCreated();
			assertEquals( defaultAspect.dir(), brjs.app("app1").defaultAspect().dir()  ); // make sure we're using the same directory for each model
		when( brjs.app("app1").defaultAspect().testType("unit").defaultTestTech() ).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then( brjs.app("app1").defaultAspect().testType("unit").defaultTestTech() ).bundledFilesEquals(defaultAspect.assetLocation("src").file("App.js"))
    		.and(response).containsCommonJsClasses("appns/App");
	}
	
}
