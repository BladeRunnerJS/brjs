package org.bladerunnerjs.model.navigation;

import java.io.File;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.NodeTesterFactory;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.model.TestModelAccessor;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TemplateGroupNavigationTest extends TestModelAccessor {

	private NodeTesterFactory<BRJS> nodeTesterFactory;
	private final File testBase = new File("src/test/resources/BRJSTest");
	private BRJS brjs;
	
	@Before
	public void setup() throws Exception
	{
		brjs = createModel(testBase, new TestLoggerFactory(new LogMessageStore()));
		nodeTesterFactory = new NodeTesterFactory<>(brjs, BRJS.class);
	}
	
	@After
	public void teardown()
	{
		brjs.close();
	}
	
	@Test
	public void templateGroupsTest()
	{
		nodeTesterFactory.createSetTester(TemplateGroup.class, "templateGroups", "templateGroup")
			.addChild("t1", "conf/templates/default/t1")
			.addChild("t2", "conf/templates/default/t2");
	}

}
