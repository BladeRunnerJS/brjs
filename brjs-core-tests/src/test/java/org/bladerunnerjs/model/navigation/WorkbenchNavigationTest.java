package org.bladerunnerjs.model.navigation;

import java.io.File;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.NodeTesterFactory;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.bladerunnerjs.model.BladeWorkbench;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WorkbenchNavigationTest
{
	private BRJS brjs;
	private NodeTesterFactory<BladeWorkbench> nodeTesterFactory;
	private BladeWorkbench workbench;
	
	@Before
	public void setup() throws Exception
	{
		brjs = BRJSTestModelFactory.createModel(new File("src/test/resources/BRJSTest"));
		workbench = brjs.app("a1").bladeset("bs1").blade("b1").workbench();
		nodeTesterFactory = new NodeTesterFactory<>(workbench, BladeWorkbench.class);
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
			.assertModelIsOK();
	}
	
	@Test
	public void styleResources()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "styleResources", "resources/style").assertModelIsOK();
	}
}
