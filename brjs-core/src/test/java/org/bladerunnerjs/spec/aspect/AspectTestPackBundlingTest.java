package org.bladerunnerjs.spec.aspect;

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
	private TestPack aspectTest;
	private Bladeset bladeset;
	private Blade blade;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			aspectTest = aspect.testType("UT").testTech("TEST_TECH");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
	}
	
	@Ignore
	@Test
	public void weBundleFilesInABladeInAnAspectTest() throws Exception {
		given(blade).hasClass("novox.Class1")
			.and(aspectTest).testRefersTo("novox.Class1");
		then(aspectTest).bundledFilesEquals(blade.src().file("novox/Class1.js"));
	}
	
}
