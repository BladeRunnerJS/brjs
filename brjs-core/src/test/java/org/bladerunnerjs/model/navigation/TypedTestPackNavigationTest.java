package org.bladerunnerjs.model.navigation;

import java.io.File;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.NodeTesterFactory;
import org.bladerunnerjs.model.TestModelAccessor;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.TypedTestPack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TypedTestPackNavigationTest extends TestModelAccessor
{
	private BRJS brjs;
	private TypedTestPack typedTestPack;
	private NodeTesterFactory<TypedTestPack> nodeTesterFactory;
	
	@Before
	public void setUp() throws Exception {
		brjs = createModel(new File("src/test/resources/BRJSTest"));
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
