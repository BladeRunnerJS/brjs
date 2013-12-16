package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppNavigationTest
{
	private App app;

	private NodeTesterFactory<App> nodeTesterFactory;
	
	@Before
	public void setup()
	{
		app = BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest")).app("a1");
		nodeTesterFactory = new NodeTesterFactory<>(app, App.class);
	}
	
	@After
	public void teardown()
	{
		app = null;
		nodeTesterFactory = null;
	}

	@Test
	public void bladesets()
	{
		nodeTesterFactory.createSetTester(Bladeset.class, "bladesets", "bladeset").addChild("bs1", "bs1-bladeset").addChild("bs2", "bs2-bladeset").assertModelIsOK();
	}

	@Test
	public void aspects()
	{
		nodeTesterFactory.createSetTester(Aspect.class, "aspects", "aspect").addChild("a1", "a1-aspect").addChild("a2", "a2-aspect").assertModelIsOK();
	}
	
	@Test
	public void jsLibs()
	{
		nodeTesterFactory.createSetTester(JsLib.class, "jsLibs", "jsLib")
			.addChild("l1", "libs/l1")
			.addChild("br", "../../sdk/libs/javascript/caplin")
			.addChild("thirdparty-l2", "../../sdk/libs/javascript/thirdparty/thirdparty-l2")
			.addChild("thirdparty-l1", "../../sdk/libs/javascript/thirdparty/thirdparty-l1")
			.assertModelIsOK();
	}
}
