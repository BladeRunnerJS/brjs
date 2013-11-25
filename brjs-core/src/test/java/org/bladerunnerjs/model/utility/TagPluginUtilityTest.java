package org.bladerunnerjs.model.utility;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.bladerunnerjs.core.plugin.taghandler.VirtualProxyTagHandlerPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.bladerunnerjs.testing.utility.MockPluginLocator;
import org.bladerunnerjs.testing.utility.MockTagHandler;
import org.junit.*;

import static org.junit.Assert.*;

public class TagPluginUtilityTest
{

	BRJS brjs;
	App app;
	Aspect aspect;
	
	@Before
	public void setup() throws Exception
	{
		MockPluginLocator mockPluginLocator = new MockPluginLocator();
		
		mockPluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin( new MockTagHandler("tag", "replaced tag!", "") ) );
		mockPluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin( new MockTagHandler("amIDevOrProd", "dev", "prod") ) );
		
		File tempDir = createTestSdkDirectory();
		brjs = BRJSTestFactory.createBRJS(tempDir, mockPluginLocator);
		
		app = brjs.app("app");
			app.create();
		aspect = app.aspect("default");
			aspect.create();
	}
	
	@Test
	public void testFilteringContentWithoutAnyTags() throws Exception
	{
		filterAndAssert( "I don't contain any tags...", "I don't contain any tags...", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void testFilteringTagWithoutAttributes() throws Exception
	{
		filterAndAssert( "this is a <@tag/>", "this is a replaced tag!\n", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	@Test
	public void testFilteringTagWithSpacesAtEnd() throws Exception
	{
		filterAndAssert( "this is a <@tag  />", "this is a replaced tag!\n", aspect.getBundleSet(), RequestMode.Dev, "");
	}

	@Test
	public void testFilteringTagWithAttributes() throws Exception
	{
		filterAndAssert( "this is a <@tag key=\"value\"/>", "this is a replaced tag!\nkey=value\n", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void testFilteringTagWithAttributesWithExtraSpaces() throws Exception
	{
		filterAndAssert( "this is a <@tag   key=\"value\"     />", "this is a replaced tag!\nkey=value\n", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void testFilteringTagWithMultipleAttributes() throws Exception
	{
		filterAndAssert( "this is a <@tag key=\"value\" key2=\"value2\"/>", "this is a replaced tag!\nkey=value\nkey2=value2\n", aspect.getBundleSet(), RequestMode.Dev, "");
	}

	@Test
	public void testFilteringTagInProdAndDevMode() throws Exception
	{
		filterAndAssert( "<@amIDevOrProd/>", "dev\n", aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "<@amIDevOrProd/>", "prod\n", aspect.getBundleSet(), RequestMode.Prod, "");
	}
	
	
	
	private void filterAndAssert(String input, String expectedOutput, BundleSet bundleSet, RequestMode opMode, String locale) throws Exception
	{
		StringWriter writer = new StringWriter();
		TagPluginUtility.filterContent(input, bundleSet, writer, opMode, locale);
		assertEquals(expectedOutput, writer.toString());
	}
	
	private File createTestSdkDirectory() {
		File sdkDir;
		
		try {
			sdkDir = FileUtility.createTemporaryDirectory("test");
			new File(sdkDir, "sdk").mkdirs();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return sdkDir;
	}
	
}
