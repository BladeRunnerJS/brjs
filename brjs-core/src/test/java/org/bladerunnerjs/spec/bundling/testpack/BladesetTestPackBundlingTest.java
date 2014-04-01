package org.bladerunnerjs.spec.bundling.testpack;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.model.exception.ModelOperationException;
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
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
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
			.and(bladesetUTs).containsFile("src-test/pkg/Util.js")
			.and(bladeset).hasClasses("appns.bs.Class1")
			.and(bladesetUTs).classExtends("pkg.Util", "appns.bs.Class1")
			.and(bladesetUTs).testRefersTo("pkg/test.js", "pkg.Util");
		then(bladesetUTs).bundledFilesEquals(
			bladeset.assetLocation("src").file("appns/bs/Class1.js"),
			bladesetUTs.testSource().file("pkg/Util.js"));
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
		when(bladesetATs).bundleSetGenerated();
		then(exceptions).verifyException(InvalidRequirePathException.class, "appns/bs/b1/Class1")
			.whereTopLevelExceptionIs(ModelOperationException.class);
	}
}
