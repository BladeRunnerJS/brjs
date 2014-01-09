package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.TypedTestPack;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.Before;
import org.junit.Test;

public class BladesetNavigationTest
{
	private NodeTesterFactory<Bladeset> nodeTesterFactory;
	private BRJS brjs;
	private Bladeset bladeset;
	
	@Before
	public void setup()
	{
		brjs = BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest"));
		bladeset = brjs.app("a1").bladeset("bs1");
		nodeTesterFactory = new NodeTesterFactory<>(bladeset, Bladeset.class);
	}
	
	@Test
	public void themes()
	{
		nodeTesterFactory.createSetTester(Theme.class, "themes", "theme")
			.addChild("t1", "themes/t1")
			.addChild("t2", "themes/t2")
			.assertModelIsOK();
	}

	@Test
	public void blades()
	{
		nodeTesterFactory.createSetTester(Blade.class, "blades", "blade")
			.addChild("b1", "blades/b1")
			.addChild("b2", "blades/b2")
			.assertModelIsOK();
	}
	
	@Test
	public void testTypes()
	{
		nodeTesterFactory.createSetTester(TypedTestPack.class, "testTypes", "testType")
			.addChild("type1", "tests/test-type1")
			.addChild("type2", "tests/test-type2")
			.assertModelIsOK();
	}
}
