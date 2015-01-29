package org.bladerunnerjs.model.navigation;

import java.io.File;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.model.NodeTesterFactory;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.bladerunnerjs.model.BladeWorkbench;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BladeNavigationTest
{
	private BRJS brjs;
	private NodeTesterFactory<Blade> nodeTesterFactory;
	private Blade blade;
	
	@Before
	public void setup() throws Exception
	{
		brjs = BRJSTestModelFactory.createModel(new File("src/test/resources/BRJSTest"));
		blade = brjs.app("a1").bladeset("bs1").blade("b1");
		nodeTesterFactory = new NodeTesterFactory<>(blade, Blade.class);
	}
	
	@After
	public void teardown()
	{
		brjs.close();
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
		nodeTesterFactory.createItemTester(BladeWorkbench.class, "workbench", "workbench").assertModelIsOK();
	}
	
}
