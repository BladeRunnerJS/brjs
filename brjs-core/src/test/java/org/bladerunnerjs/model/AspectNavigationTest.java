package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.TypedTestPack;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AspectNavigationTest
{
	private NodeTesterFactory<Aspect> nodeTesterFactory;
	private Aspect aspect;
	
	@Before
	public void setup()
	{
		aspect = BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest")).app("a1").aspect("a1");
		nodeTesterFactory = new NodeTesterFactory<>(aspect, Aspect.class);
	}
	
	@After
	public void teardown()
	{
		aspect = null;
		nodeTesterFactory = null;
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
	
	@Test
	public void src()
	{
		nodeTesterFactory.createItemTester(ShallowAssetLocation.class, "src", "src").assertModelIsOK();
	}
	
	@Test
	public void resources()
	{
		nodeTesterFactory.createItemTester(DeepAssetLocation.class, "resources", "resources").assertModelIsOK();
	}
}
