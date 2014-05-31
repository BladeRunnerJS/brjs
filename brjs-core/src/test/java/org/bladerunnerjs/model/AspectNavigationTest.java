package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.Aspect;
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
	private BRJS brjs;
	
	@Before
	public void setup() throws Exception
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
	public void testTypes()
	{
		nodeTesterFactory.createSetTester(TypedTestPack.class, "testTypes", "testType")
			.addChild("type1", "tests/test-type1")
			.addChild("type2", "tests/test-type2")
			.assertModelIsOK();
	}
}
