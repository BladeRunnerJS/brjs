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
			.and(aspectUTs).hasClass("appTestUtils.Class1")
			.and(aspectATs).hasClass("appTestUtils.Class1")
			.and(aspectUTs).testRefersTo("pkg/test.js", "appTestUtils.Class1");
		then(aspectUTs).bundledFilesEquals(
				aspectUTs.testSource().file("appTestUtils/Class1.js"));
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
			.and(aspectUTs).hasClass("aspectUT.Class1")
			.and(aspect).hasTestClass("appns.Class1")
			.and(aspectUTs).testRefersTo("pkg/test.js", "aspectUT.Class1")
			.and(aspectUTs).classDependsOn("aspectUT.Class1", "appns.Class1");
		then(aspectUTs).bundledFilesEquals(
				aspectUTs.testSource().file("aspectUT/Class1.js"),
				aspect.assetLocation("src-test").file("appns/Class1.js"));
	}
	
	@Test
	public void testCodeCanUseRequires() throws Exception {
		given(aspect).hasNodeJsPackageStyle()
    		.and(aspect).hasClasses("appns/Class1")
    		.and(aspectUTs).testRequires("pkg/test.js", "appns/Class1");
    	then(aspectUTs).bundledFilesEquals(aspect.assetLocation("src").file("appns/Class1.js"));
	}
}
