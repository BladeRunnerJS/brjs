package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPackNavigationTest
{
	private BRJS brjs;
	private TestPack technologyTestPack;
	private NodeTesterFactory<TestPack> nodeTesterFactory;
	
	@Before
	public void setUp() {
		brjs = BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest"));
		technologyTestPack = brjs.app("a1").bladeset("bs1").testType("type1").testTech("tech1");
		nodeTesterFactory = new NodeTesterFactory<>(technologyTestPack, TestPack.class);
	}
	
	@Test
	public void testSource()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "testSource", "src-test").assertModelIsOK();
	}

	@Test
	public void tests()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "tests", "tests").assertModelIsOK();
	}
}
