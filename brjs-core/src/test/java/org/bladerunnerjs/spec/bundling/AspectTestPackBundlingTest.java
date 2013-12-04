package org.bladerunnerjs.spec.bundling;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class AspectTestPackBundlingTest extends SpecTest
{
	private App app;
	private Aspect aspect;
	private TestPack aspectUTs, aspectATs;
	private Bladeset bladeset;
	private Blade blade;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			aspectUTs = aspect.testType("unit").testTech("TEST_TECH");
			aspectATs = aspect.testType("acceptance").testTech("TEST_TECH");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
	}
	
	// TODO remove the @Ignores
	@Ignore
	@Test
	public void weBundleAspectFilesInUTs() throws Exception {
		given(blade).hasClass("mypkg.Class1")
			.and(aspectUTs).testRefersTo("mypkg.Class1");
		then(aspectUTs).bundledFilesEquals(blade.src().file("mypkg/Class1.js"));
	}
	
	@Ignore
	@Test
	public void weBundleAspectFilesInATs() throws Exception {
		given(blade).hasClass("mypkg.Class1")
			.and(aspectUTs).testRefersTo("mypkg.Class1");
		then(aspectATs).bundledFilesEquals(blade.src().file("mypkg/Class1.js"));
	}
	
}
