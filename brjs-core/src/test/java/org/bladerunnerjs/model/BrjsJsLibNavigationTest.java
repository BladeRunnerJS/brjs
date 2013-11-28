package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.BrjsJsLib;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BrjsJsLibNavigationTest
{
	private NodeTesterFactory<BrjsJsLib> nodeTesterFactory;
	
	@Before
	public void setup()
	{
		nodeTesterFactory = new NodeTesterFactory<BrjsJsLib>((BrjsJsLib) BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest")).sdkLib(), BrjsJsLib.class);
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