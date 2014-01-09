package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.TypedTestPack;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BladeNavigationTest
{
	private BRJS brjs;
	private NodeTesterFactory<Blade> nodeTesterFactory;
	private Blade blade;
	
	@Before
	public void setup()
	{
		brjs = BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest"));
		blade = brjs.app("a1").bladeset("bs1").blade("b1");
		nodeTesterFactory = new NodeTesterFactory<>(blade, Blade.class);
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
	public void testTypes()
	{
		nodeTesterFactory.createSetTester(TypedTestPack.class, "testTypes", "testType")
			.addChild("type1", "tests/test-type1")
			.addChild("type2", "tests/test-type2")
			.assertModelIsOK();
	}
	
	@Test
	public void workbench()
	{
		nodeTesterFactory.createItemTester(Workbench.class, "workbench", "workbench").assertModelIsOK();
	}
	
}
