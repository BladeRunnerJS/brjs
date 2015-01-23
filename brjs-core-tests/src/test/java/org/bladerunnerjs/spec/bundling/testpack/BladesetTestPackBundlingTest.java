package org.bladerunnerjs.spec.bundling.testpack;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BladesetTestPackBundlingTest extends SpecTest
{
	private App app;
	private Bladeset bladeset;
	private Blade blade;
	private TestPack bladesetUTs, bladesetATs;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			bladeset = app.bladeset("bs");
			bladesetUTs = bladeset.testType("unit").testTech("TEST_TECH");
			bladesetATs = bladeset.testType("acceptance").testTech("TEST_TECH");
			blade = bladeset.blade("b1");
			
	}

	// N A M E S P A C E D - J S
	@Test
	public void weBundleBladesetFilesInUTs() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classDependsOn("appns.bs.Class1", "appns.bs.Class2")
			.and(bladesetUTs).testRefersTo("pkg/test.js", "appns.bs.Class1");
		then(bladesetUTs).bundledFilesEquals(
				bladeset.assetLocation("src").file("appns/bs/Class1.js"),
				bladeset.assetLocation("src").file("appns/bs/Class2.js"));
	}
	
	@Test
	public void weBundleBladesetFilesInATs() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classDependsOn("appns.bs.Class1", "appns.bs.Class2")
			.and(bladesetATs).testRefersTo("pkg/test.js", "appns.bs.Class1");
		then(bladesetATs).bundledFilesEquals(
				bladeset.assetLocation("src").file("appns/bs/Class1.js"),
				bladeset.assetLocation("src").file("appns/bs/Class2.js"));
	}
	
	@Test
	public void weBundleBladesetSrcTestContentsInUTs() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladesetUTs).containsFile("src-test/appns/bs/Util.js")
			.and(bladeset).hasClasses("appns.bs.Class1")
			.and(bladesetUTs).classExtends("appns.bs.Util", "appns.bs.Class1")
			.and(bladesetUTs).testRefersTo("pkg/test.js", "appns.bs.Util");
		then(bladesetUTs).bundledFilesEquals(
			bladeset.assetLocation("src").file("appns/bs/Class1.js"),
			bladesetUTs.testSource().file("appns/bs/Util.js"));
	}
	
	@Test
	public void noExceptionsAreThrownIfTheBladesetSrcFolderHasAHiddenFolder() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classDependsOn("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).hasDir("src/.svn")
			.and(bladesetATs).testRefersTo("pkg/test.js", "appns.bs.Class1");
		then(bladesetATs).bundledFilesEquals(
			bladeset.assetLocation("src").file("appns/bs/Class1.js"),
			bladeset.assetLocation("src").file("appns/bs/Class2.js"));
	}
	
	@Test
	public void bladesetTestsCannotDependOnBlades() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classDependsOn("appns.bs.Class1", "appns.bs.Class2")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1")
			.and(bladesetATs).testRefersTo("pkg/test.js", "appns.bs.Class1", "appns.bs.b1.Class1");
		then(bladesetATs).bundledFilesEquals(
			bladeset.assetLocation("src").file("appns/bs/Class1.js"),
			bladeset.assetLocation("src").file("appns/bs/Class2.js"));
	}
}
