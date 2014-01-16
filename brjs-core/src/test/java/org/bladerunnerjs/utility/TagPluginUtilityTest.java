package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRJSConformantAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyTagHandlerPlugin;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.bladerunnerjs.testing.utility.MockPluginLocator;
import org.bladerunnerjs.testing.utility.MockTagHandler;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.NoTagHandlerFoundException;
import org.bladerunnerjs.utility.TagPluginUtility;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class TagPluginUtilityTest
{

	@Rule
	public ExpectedException exception = ExpectedException.none();

	
	BRJS brjs;
	App app;
	Aspect aspect;
	
	@Before
	public void setup() throws Exception
	{
		MockPluginLocator mockPluginLocator = new MockPluginLocator();
		
		/* valid tags */
		mockPluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin( new MockTagHandler("tag", "replaced tag!", "") ) );
		mockPluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin( new MockTagHandler("amIDevOrProd", "dev", "prod") ) );
		mockPluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin( new MockTagHandler("a.tag", "replaced tag!", "") ) );
		mockPluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin( new MockTagHandler("a_tag", "replaced tag!", "") ) );
		mockPluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin( new MockTagHandler("a-tag", "replaced tag!", "") ) );
		
		/* invalid valid tags */
		mockPluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin( new MockTagHandler("1tag", "replaced tag!", "") ) );
		mockPluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin( new MockTagHandler("-tag", "replaced tag!", "") ) );
		
		/* asset location support */
		mockPluginLocator.assetLocationPlugins.add(new VirtualProxyAssetLocationPlugin(new BRJSConformantAssetLocationPlugin()));
		
		File tempDir = createTestSdkDirectory();
		brjs = BRJSTestFactory.createBRJS(tempDir, mockPluginLocator);
		
		app = brjs.app("app");
			app.create();
		aspect = app.aspect("default");
			aspect.create();
	}
	
	@After
	public void tearDown() {
		brjs.close();
	}
	
	@Test
	public void testFilteringContentWithoutAnyTags() throws Exception
	{
		filterAndAssert( "I don't contain any tags...", "I don't contain any tags...", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void testFilteringTagWithoutAttributes() throws Exception
	{
		filterAndAssert( "this is a <@tag@/>", "this is a replaced tag!", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void testFilteringTagWithSpacesAtEnd() throws Exception
	{
		filterAndAssert( "this is a <@tag  @/>", "this is a replaced tag!", aspect.getBundleSet(), RequestMode.Dev, "");
	}

	@Test
	public void testFilteringTagWithAttributes() throws Exception
	{
		filterAndAssert( "this is a <@tag key=\"value\"@/>", String.format("this is a replaced tag!%nkey=value"), aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void testFilteringTagWithAttributesWithExtraSpaces() throws Exception
	{
		filterAndAssert( "this is a <@tag   key=\"value\"     @/>", String.format("this is a replaced tag!%nkey=value"), aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void testFilteringTagWithAttributesWithHyphens() throws Exception
	{
		filterAndAssert( "this is a <@tag the-key=\"value\"@/>", String.format("this is a replaced tag!%nthe-key=value"), aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void testFilteringTagWithMultipleAttributes() throws Exception
	{
		filterAndAssert( "this is a <@tag key=\"value\" key2=\"value2\"@/>", String.format("this is a replaced tag!%nkey=value%nkey2=value2"), aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void attributesCanContainHypens() throws Exception
	{
		filterAndAssert( "this is a <@tag a-key=\"a-value\" @/>", String.format("this is a replaced tag!%na-key=a-value"), aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void attributeValuesCanBeInSingleOrDoubleQuotes() throws Exception
	{
		filterAndAssert( "<@tag a-key=\"a-value\" @/>", String.format("replaced tag!%na-key=a-value"), aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "<@tag a-key='a-value' @/>", String.format("replaced tag!%na-key=a-value"), aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void multipleTagsAreReplaced() throws Exception
	{
		filterAndAssert( "<@tag  @/> and another <@tag a-key=\"a-value\" @/>", String.format("replaced tag! and another replaced tag!%na-key=a-value"), aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "<@tag  @/>\nand another <@tag a-key=\"a-value\" @/>", String.format("replaced tag!\nand another replaced tag!%na-key=a-value"), aspect.getBundleSet(), RequestMode.Dev, "");
	}

	@Test
	public void testFilteringTagInProdAndDevMode() throws Exception
	{
		filterAndAssert( "<@amIDevOrProd@/>", "dev", aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "<@amIDevOrProd@/>", "prod", aspect.getBundleSet(), RequestMode.Prod, "");
	}
	
	@Test
	public void tagsMustMatchExactly() throws Exception
	{
		filterAndAssert( "this is a < @tag@/>", "this is a < @tag@/>", aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "this is a <@ tag@/>", "this is a <@ tag@/>", aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "this is a <@tag@ >", "this is a <@tag@ >", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void tagsCanContainSeperatorChars() throws Exception
	{		
		filterAndAssert( "<@a.tag@/>", "replaced tag!", aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "<@a_tag@/>", "replaced tag!", aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "<@a-tag@/>", "replaced tag!", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void tagsCannotStartWithNumbersOrSeperatorCharsAndMustBeValidXmlTags() throws Exception
	{		
		filterAndAssert( "<@1tag@/>", "<@1tag@/>", aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "<@-tag@/>", "<@-tag@/>", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void badlyFormedXmlIsntParsedAsATag() throws Exception
	{		
		filterAndAssert( "<@tag ~=* @/>", "<@tag ~=* @/>", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Ignore //TODO: add this test back in once old tag handlers have been moved across to new style plugins
	@Test
	public void exceptionIsThrownIfTagHandlerCantBeFound() throws Exception
	{		
		exception.expect(NoTagHandlerFoundException.class);
		exception.expectMessage("tag1");
		filterAndAssert( "<@tag1 @/>", "", aspect.getBundleSet(), RequestMode.Dev, "");
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
