package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.TypedTestPack;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.Before;
import org.junit.Test;

public class AspectNavigationTest
{
	private NodeTesterFactory<Aspect> nodeTesterFactory;
	private Aspect aspect;
	private BRJS brjs;
	
	@Before
	public void setup()
	{
		brjs = BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest"));
		aspect = brjs.app("a1").aspect("a1");
		nodeTesterFactory = new NodeTesterFactory<>(aspect, Aspect.class);
	}
	
	@After
	public void teardown()
	{
		brjs.close();
	}
	
	@Test
	public void unbundledResources()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "unbundledResources", "unbundled-resources").assertModelIsOK();
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
}
