package org.bladerunnerjs.model.navigation;

import java.io.File;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.model.NodeTesterFactory;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TypedTestPackNavigationTest
{
	private BRJS brjs;
	private TypedTestPack typedTestPack;
	private NodeTesterFactory<TypedTestPack> nodeTesterFactory;
	
	@Before
	public void setUp() throws Exception {
		brjs = BRJSTestModelFactory.createModel(new File("src/test/resources/BRJSTest"));
		typedTestPack = brjs.app("a1").bladeset("bs1").testType("type1");
		nodeTesterFactory = new NodeTesterFactory<>(typedTestPack, TypedTestPack.class);
	}
	
	@After
	public void tearDown() {
		brjs.close();
	}
	
	@Test
	public void testTechPacks()
	{
		nodeTesterFactory.createSetTester(TestPack.class, "testTechs", "testTech").addChild("tech1", "tech1").addChild("tech2", "tech2").assertModelIsOK();
	}
}
