package org.bladerunnerjs.spec.bundling;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BladeTestPackBundlingTest extends SpecTest
{
	private App app;
	private Bladeset bladeset;
	private Blade blade;
	private TestPack bladeUTs, bladeATs;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
    		.and(brjs).automaticallyFindsMinifiers()
    		.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeUTs = blade.testType("unit").testTech("TEST_TECH");
			bladeATs = blade.testType("acceptance").testTech("TEST_TECH");
	}
	
	@Test
	public void weBundleBladeFilesInUTs() throws Exception {
		given(blade).hasPackageStyle("", "namespaced-js")
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classRefersTo("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(bladeUTs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeUTs).bundledFilesEquals(
				blade.assetLocation("src").file("src/appns/bs/b1/Class1.js"),
				blade.assetLocation("src").file("src/appns/bs/b1/Class2.js"));
	}
	
	@Test
	public void weBundleBladeFilesInATs() throws Exception {
		given(blade).hasPackageStyle("", "namespaced-js")
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classRefersTo("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(bladeUTs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).bundledFilesEquals(
				blade.assetLocation("src").file("src/appns/bs/b1/Class1.js"),
				blade.assetLocation("src").file("src/appns/bs/b1/Class2.js"));
	}
	
	@Test
	public void weBundleBladeSrcTestContentsInUTs() throws Exception {
		given(blade).hasPackageStyle("", "namespaced-js")
			.and(blade).hasClasses("appns.bs.b1.Class1")
			.and(bladeUTs).containsFile("src/js-test-driver/src-test/util.js")
			.and(bladeUTs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeUTs).bundledFilesEquals(
			blade.assetLocation("src").file("src/appns/bs/b1/Class1.js"),
			blade.assetLocation("src").file("src/appns/bs/b1/Class2.js"),
			bladeUTs.testSource().file("util.js"));
	}
	
	@Test
	public void noExceptionsAreThrownIfTheBladeSrcFolderHasAHiddenFolder() throws Exception {
		given(blade).hasPackageStyle("", "namespaced-js")
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classRefersTo("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).hasDir("src/.svn")
			.and(bladeUTs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).bundledFilesEquals(
			blade.assetLocation("src").file("src/appns/bs/b1/Class1.js"),
			blade.assetLocation("src").file("src/appns/bs/b1/Class2.js"));
	}
}
