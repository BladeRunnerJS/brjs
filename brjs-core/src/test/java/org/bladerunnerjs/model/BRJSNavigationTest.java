package org.bladerunnerjs.model;

import static org.junit.Assert.*;

import java.io.File;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BRJSNavigationTest
{
	private NodeTesterFactory<BRJS> nodeTesterFactory;
	private final File testBase = new File("src/test/resources/BRJSTest");
	private BRJS brjs;
	
	@Before
	public void setup()
	{
		brjs = BRJSTestFactory.createBRJS(testBase);
		nodeTesterFactory = new NodeTesterFactory<>(brjs, BRJS.class);
	}
	
	@After
	public void teardown()
	{
		brjs.close();
	}

	@Test
	public void apps()
	{
		nodeTesterFactory.createSetTester(App.class, "apps", "app")
			.addChild("a1", "apps/a1")
			.addChild("a2", "apps/a2")
			.assertModelIsOK();
	}
	
	@Test
	public void systemApps()
	{
		nodeTesterFactory.createSetTester(App.class, "systemApps", "systemApp")
			.addChild("sa1", "sdk/system-applications/sa1")
			.addChild("sa2", "sdk/system-applications/sa2")
			.assertModelIsOK();
	}
	
	@Test
	public void sdkLib()
	{
		nodeTesterFactory.createSetTester(JsLib.class, "sdkLibs", "sdkLib")
    		.addChild("br", "sdk/libs/javascript/br-libs/br")
    		.addChild("brlib2", "sdk/libs/javascript/br-libs/brlib2")
    		.assertModelIsOK();
	}
	
	@Test
	public void sdkNonBladeRunnerLibs()
	{
		nodeTesterFactory.createSetTester(JsLib.class, "sdkNonBladeRunnerLibs", "sdkNonBladeRunnerLib")
			.addChild("thirdparty-l1", "sdk/libs/javascript/thirdparty/thirdparty-l1")
			.addChild("thirdparty-l2", "sdk/libs/javascript/thirdparty/thirdparty-l2")
			.assertModelIsOK();
	}
	
	@Test
	public void jsPatches()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "jsPatches", "js-patches")
			.assertModelIsOK();
	}
	
	@Test
	public void templates()
	{
		nodeTesterFactory.createSetTester(NamedDirNode.class, "templates", "template")
			.addChild("t1", "sdk/templates/t1-template")
			.addChild("t2", "sdk/templates/t2-template")
			.assertModelIsOK();
	}
	
	@Test
	public void testResults()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "testResults", "sdk/test-results")
			.assertModelIsOK();
	}
	
	@Test
	public void appJars()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "appJars", "sdk/libs/java/application")
			.assertModelIsOK();
	}
	
	@Test
	public void systemJars()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "systemJars", "sdk/libs/java/system")
			.assertModelIsOK();
	}
	
	@Test
	public void testJars()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "testJars", "sdk/libs/java/testRunner")
			.assertModelIsOK();
	}
	
	@Test
	public void userJars()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "userJars", "conf/java")
			.assertModelIsOK();
	}
	
	@Test
	public void logs()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "logs", "sdk/log")
			.assertModelIsOK();
	}
	
	@Test
	public void apiDocs()
	{
		nodeTesterFactory.createItemTester(DirNode.class, "apiDocs", "sdk/docs/jsdoc")
			.assertModelIsOK();
	}
	
	@Test
	public void testGettingLoggerForClass() throws Exception
	{
		Logger logger = brjs.logger(LoggerType.BUNDLER, this.getClass());
		assertEquals("brjs.bundler.BRJSNavigationTest", logger.getName());
	}
}
