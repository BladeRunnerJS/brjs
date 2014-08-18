package org.bladerunnerjs.spec.bundling.testpack;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class TestPackBundlingTest extends SpecTest
{
	private App app;
	private Aspect aspect;
	private TestPack aspectUTs, aspectATs;
	private StringBuffer response = new StringBuffer();
	
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
	
	
	
}
