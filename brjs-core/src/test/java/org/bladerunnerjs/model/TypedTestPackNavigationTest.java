package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.TypedTestPack;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.Test;

public class TypedTestPackNavigationTest
{
	private TypedTestPack typedTestPack = BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest")).app("a1").bladeset("bs1").testType("type1");
	private NodeTesterFactory<TypedTestPack> nodeTesterFactory = new NodeTesterFactory<>(typedTestPack, TypedTestPack.class);
	
	@Test
	public void testTechPacks()
	{
		nodeTesterFactory.createSetTester(TestPack.class, "testTechs", "testTech").addChild("tech1", "tech1").addChild("tech2", "tech2").assertModelIsOK();
	}
}
