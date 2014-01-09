package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.TypedTestPack;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.Before;
import org.junit.Test;

public class TypedTestPackNavigationTest
{
	private BRJS brjs;
	private TypedTestPack typedTestPack;
	private NodeTesterFactory<TypedTestPack> nodeTesterFactory;
	
	@Before
	public void setUp() {
		brjs = BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest"));
		typedTestPack = brjs.app("a1").bladeset("bs1").testType("type1");
		nodeTesterFactory = new NodeTesterFactory<>(typedTestPack, TypedTestPack.class);
	}
	
	@Test
	public void testTechPacks()
	{
		nodeTesterFactory.createSetTester(TestPack.class, "testTechs", "testTech").addChild("tech1", "tech1").addChild("tech2", "tech2").assertModelIsOK();
	}
}
