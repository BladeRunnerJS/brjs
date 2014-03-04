package org.bladerunnerjs.spec.bundling.testpack;

import static org.bladerunnerjs.model.BundleSetCreator.Messages.BUNDLABLE_NODE_SEED_FILES_MSG;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BundleSetCreator;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class TestPackBundlingTest extends SpecTest
{
	private App app;
	private Aspect aspect;
	private TestPack aspectUTs, aspectATs;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			aspectUTs = aspect.testType("unit").testTech("TEST_TECH");
			aspectATs = aspect.testType("acceptance").testTech("TEST_TECH");
	}
	
	@Test
	public void twoTestPacksCanHaveTestsWithTheSameRequirePath() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspectUTs).hasClass("appTestUtils.Class1")
			.and(aspectATs).hasClass("appTestUtils.Class1")
			.and(aspectUTs).testRefersTo("pkg/test.js", "appTestUtils.Class1");
		then(aspectUTs).bundledFilesEquals(
				aspectUTs.testSource().file("appTestUtils/Class1.js"));
	}
	
	@Test
	public void srcTestCanLiveAtTestsAndTechnologyLevel() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspectUTs).hasClass("aspectUT.Class1")
			.and(aspect).hasTestClass("appns.Class1")
			.and(aspectUTs).testRefersTo("pkg/test.js", "aspectUT.Class1")
			.and(aspectUTs).classRefersTo("aspectUT.Class1", "appns.Class1");
		then(aspectUTs).bundledFilesEquals(
				aspectUTs.testSource().file("aspectUT/Class1.js"),
				aspect.assetLocation("src-test").file("appns/Class1.js"));
	}
	
	@Test
	public void testCodeCanUseRequires() throws Exception {
		given(aspect).hasNodeJsPackageStyle()
    		.and(aspect).hasClasses("appns.Class1")
    		.and(aspectUTs).testRequires("pkg/test.js", "appns/Class1");
    	then(aspectUTs).bundledFilesEquals(aspect.assetLocation("src").file("appns/Class1.js"));
	}
	
	@Test
	public void weGetGoodLogMessagesForTestSeedFiles() throws Exception {
		given(logging).enabled()
			.and(aspect).hasNamespacedJsPackageStyle()
    		.and(aspect).hasClasses("appns.Class1", "appns.Class2")
    		.and(aspectUTs).testRefersTo("pkg/test.js", "appns.Class1");
		when(aspectUTs).bundleSetGenerated();
		then(logging).debugMessageReceived(BundleSetCreator.Messages.BUNDLABLE_NODE_IS_TEST_PACK, unquoted("TestPack"), "TEST_TECH")
    		.and(logging).debugMessageReceived(BUNDLABLE_NODE_SEED_FILES_MSG, unquoted("TestPack"), "TEST_TECH", unquoted("'default-aspect/tests/test-unit/TEST_TECH/tests/pkg/test.js'"));
	}
	
}
