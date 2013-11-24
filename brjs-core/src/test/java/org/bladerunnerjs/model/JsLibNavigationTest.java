package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JsLibNavigationTest
{
	private NodeTesterFactory<JsLib> nodeTesterFactory;
	
	@Before
	public void setup()
	{
		nodeTesterFactory = new NodeTesterFactory<>(BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest")).sdkLib(), JsLib.class);
	}
	
	@After
	public void teardown()
	{
		nodeTesterFactory = null;
	}

	@Test
	public void src()
	{
		nodeTesterFactory.createItemTester(SourceAssetLocation.class, "src", "src").assertModelIsOK();
	}
	
	@Test
	public void resources()
	{
		nodeTesterFactory.createItemTester(DeepAssetLocation.class, "resources", "resources").assertModelIsOK();
	}
}