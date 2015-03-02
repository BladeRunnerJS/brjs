package org.bladerunnerjs.model.navigation;

import java.io.File;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.model.NodeTesterFactory;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AspectNavigationTest
{
	private NodeTesterFactory<Aspect> nodeTesterFactory;
	private Aspect aspect;
	private BRJS brjs;
	
	@Before
	public void setup() throws Exception
	{
		brjs = BRJSTestModelFactory.createModel(new File("src/test/resources/BRJSTest"));
		aspect = brjs.app("a1").aspect("a1");
		nodeTesterFactory = new NodeTesterFactory<>(aspect, Aspect.class);
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
}
