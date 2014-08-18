package org.bladerunnerjs.model.navigation;

import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NodeTesterFactory;
import org.bladerunnerjs.model.TestModelAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppNavigationTest extends TestModelAccessor
{
	private BRJS brjs;
	private App app;

	private NodeTesterFactory<App> nodeTesterFactory;

	
	@Before
	public void setup() throws Exception
	{
		brjs = createModel(new File("src/test/resources/BRJSTest"));
		app = brjs.app("a1");
		nodeTesterFactory = new NodeTesterFactory<>(app, App.class);
	}
	
	@After
	public void teardown()
	{
		brjs.close();
	}

	@Test
	public void bladesets()
	{
		nodeTesterFactory.createSetTester(Bladeset.class, "bladesets", "bladeset")
			.addChild("bs1", "bs1-bladeset")
			.addChild("bs2", "bs2-bladeset")
			.assertModelIsOK();
	}
	
	@Test
	public void defaultBladesets()
	{
		app = brjs.app("a3");
		nodeTesterFactory = new NodeTesterFactory<>(app, App.class);
		nodeTesterFactory.createSetTester(Bladeset.class, "bladesets", "bladeset")
			.addChild("default", ".")
			.addChild("bs1", "bs1-bladeset")
			.assertModelIsOK();
	}

	@Test
	public void aspects()
	{
		nodeTesterFactory.createSetTester(Aspect.class, "aspects", "aspect")
			.addChild("a1", "a1-aspect")
			.addChild("a2", "a2-aspect")
			.assertModelIsOK();
	}
	
	@Test
	public void defaultAspects()
	{
		app = brjs.app("a3");
		nodeTesterFactory = new NodeTesterFactory<>(app, App.class);
		nodeTesterFactory.createSetTester(Aspect.class, "aspects", "aspect")
			.addChild("default", ".")
			.addChild("a1", "a1-aspect")
			.assertModelIsOK();
	}
	
	@Test
	public void jsLibs()
	{
		nodeTesterFactory.createSetTester(JsLib.class, "jsLibs", "jsLib")
			.addChild("br", "../../sdk/libs/javascript/br")
			.addChild("brlib2", "../../sdk/libs/javascript/brlib2")
    		.addChild("thirdparty-l1", "../../sdk/libs/javascript/thirdparty-l1")
    		.addChild("thirdparty-l2", "../../sdk/libs/javascript/thirdparty-l2")
    		.addChild("l1", "libs/l1")
			.assertModelIsOK();
	}
}
