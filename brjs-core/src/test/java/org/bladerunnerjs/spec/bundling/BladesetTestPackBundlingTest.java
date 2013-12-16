package org.bladerunnerjs.spec.bundling;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class BladesetTestPackBundlingTest extends SpecTest
{
	private App app;
	private Bladeset bladeset;
	private TestPack bladesetUTs, bladesetATs;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app1");
			bladeset = app.bladeset("bs");
			bladesetUTs = bladeset.testType("unit").testTech("TEST_TECH");
			bladesetATs = bladeset.testType("acceptance").testTech("TEST_TECH");
	}

	// TODO remove the @Ignores
	@Ignore
	@Test
	public void weBundleBladesetFilesInUTs() throws Exception {
		given(bladeset).hasPackageStyle("src/appns/bs", "namespaced-js")
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classRefersTo("appns.bs.Class1", "appns.bs.Class2")
			.and(bladesetUTs).testRefersTo("appns.bs.Class1");
		then(bladesetUTs).bundledFilesEquals(
				bladeset.assetLocation("src").file("src/appns/bs/b1/Class1.js"),
				bladeset.assetLocation("src").file("src/appns/bs/b1/Class2.js"));
	}
	
	@Ignore
	@Test
	public void weBundleBladesetFilesInATs() throws Exception {
		given(bladeset).hasPackageStyle("src/appns/bs", "namespaced-js")
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classRefersTo("appns.bs.Class1", "appns.bs.Class2")
			.and(bladesetATs).testRefersTo("appns.bs.Class1");
		then(bladesetATs).bundledFilesEquals(
				bladeset.assetLocation("src").file("src/appns/bs/b1/Class1.js"),
				bladeset.assetLocation("src").file("src/appns/bs/b1/Class2.js"));
	}
	
	@Ignore
	@Test
	public void weBundleBladesetSrcTestContentsInUTs() throws Exception {
		given(bladeset).hasPackageStyle("src/appns/bs/b1", "namespaced-js")
			.and(bladeset).hasClasses("appns.bs.b1.Class1")
			.and(bladesetUTs).containsFile("src/js-test-driver/src-test/util.js")
			.and(bladesetUTs).testRefersTo("appns.bs.b1.Class1");
		then(bladesetUTs).bundledFilesEquals(
			bladeset.assetLocation("src").file("src/appns/bs/b1/Class1.js"),
			bladeset.assetLocation("src").file("src/appns/bs/b1/Class2.js"),
			bladesetUTs.testSource().file("util.js"));
	}
	
	@Ignore
	@Test
	public void noExceptionsAreThrownIfTheBladesetSrcFolderHasAHiddenFolder() throws Exception {
		given(bladeset).hasPackageStyle("src/appns/bs", "namespaced-js")
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classRefersTo("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).hasDir("src/.svn")
			.and(bladesetATs).testRefersTo("appns.bs.Class1");
		then(bladesetATs).bundledFilesEquals(
			bladeset.assetLocation("src").file("src/appns/bs/b1/Class1.js"),
			bladeset.assetLocation("src").file("src/appns/bs/b1/Class2.js"));
	}
}
