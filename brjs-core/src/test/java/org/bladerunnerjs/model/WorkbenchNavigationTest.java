package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.TypedTestPack;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WorkbenchNavigationTest
{
	private NodeTesterFactory<Workbench> nodeTesterFactory;
	private Workbench workbench;
	
	@Before
	public void setup()
	{
		workbench = BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest")).app("a1").bladeset("bs1").blade("b1").workbench();
		nodeTesterFactory = new NodeTesterFactory<>(workbench, Workbench.class);
	}
	
	@After
	public void teardown()
	{
		workbench = null;
		nodeTesterFactory = null;
	}
	
	@Test
	public void testTypes()
	{
		nodeTesterFactory.createSetTester(TypedTestPack.class, "testTypes", "testType")
			.addChild("type1", "tests/test-type1")
			.assertModelIsOK();
	}
	
	@Test
	public void styleResources()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "styleResources", "resources/style").assertModelIsOK();
	}
}
