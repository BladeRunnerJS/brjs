package org.bladerunnerjs.spec.bundling;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.specutil.engine.SpecTest;
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
		given(bladeset).hasPackageStyle("src/mypkg/bs", "namespaced-js")
			.and(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(bladeset).classRefersTo("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(bladesetUTs).testRefersTo("mypkg.bs.Class1");
		then(bladesetUTs).bundledFilesEquals(
				bladeset.src().file("src/mypkg/bs/b1/Class1.js"),
				bladeset.src().file("src/mypkg/bs/b1/Class2.js"));
	}
	
	@Ignore
	@Test
	public void weBundleBladesetFilesInATs() throws Exception {
		given(bladeset).hasPackageStyle("src/mypkg/bs", "namespaced-js")
			.and(bladeset).hasClasses("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(bladeset).classRefersTo("mypkg.bs.Class1", "mypkg.bs.Class2")
			.and(bladesetATs).testRefersTo("mypkg.bs.Class1");
		then(bladesetATs).bundledFilesEquals(
				bladeset.src().file("src/mypkg/bs/b1/Class1.js"),
				bladeset.src().file("src/mypkg/bs/b1/Class2.js"));
	}
}
