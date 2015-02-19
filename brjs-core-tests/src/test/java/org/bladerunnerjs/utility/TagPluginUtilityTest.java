package org.bladerunnerjs.utility;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.spec.utility.MockPluginLocator;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.bladerunnerjs.plugin.brjsconformant.BRJSConformantAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyTagHandlerPlugin;
import org.bladerunnerjs.testing.utility.MockTagHandler;
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


	private File testSdkDirectory;
	
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
		
		testSdkDirectory = BRJSTestModelFactory.createTestSdkDirectory();
		brjs = BRJSTestModelFactory.createModel(testSdkDirectory, mockPluginLocator);
		
		app = brjs.app("app");
			app.create();
		aspect = app.aspect("default");
			aspect.create();
	}

	@After
	public void tearDown() {
		brjs.close();
		org.apache.commons.io.FileUtils.deleteQuietly(testSdkDirectory);
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
	public void tagStartMustMatchExactly() throws Exception
	{
		filterAndAssert( "this is a < @tag@/>", "this is a < @tag@/>", aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "this is a <@ tag@/>", "this is a <@ tag@/>", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void extraWhitespaceCanPrefixTheClosingBrackets() throws Exception
	{
		filterAndAssert( "this is a <@tag@ />", "this is a replaced tag!", aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "this is a <@tag@/ >", "this is a replaced tag!", aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "this is a <@tag@ / >", "this is a replaced tag!", aspect.getBundleSet(), RequestMode.Dev, "");
		filterAndAssert( "this is a <@tag @ / >", "this is a replaced tag!", aspect.getBundleSet(), RequestMode.Dev, "");
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
	
	@Test
	public void exceptionIsThrownIfTagHandlerCantBeFound() throws Exception
	{		
		exception.expect(NoTagHandlerFoundException.class);
		exception.expectMessage("tag1");
		filterAndAssert( "<@tag1 @/>", "", aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	
	@Test
	public void correctFilterMapIsReturnedIsNoTagIsFound() throws Exception
	{		
		Map<String,Map<String,String>> expecetedResult = new HashMap<>();
		filterAndAssertMapReturned( "I don't contain any tags...", expecetedResult, aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	
	@Test
	public void correctFilterMapIsReturnedWhenATagIsFound() throws Exception
	{
		Map<String,Map<String,String>> expecetedResult = new HashMap<>();
		expecetedResult.put("tag",new HashMap<>());
		filterAndAssertMapReturned( "this is a <@tag@/>", expecetedResult, aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	@Test
	public void correctFilterMapIsReturnedWhenATagIsFoundWithAttributes() throws Exception
	{
		Map<String,Map<String,String>> expecetedResult = new HashMap<>();
		expecetedResult.put("tag",new HashMap<>());
		expecetedResult.get("tag").put("key", "value");
		filterAndAssertMapReturned( "this is a <@tag key=\"value\"@/>", expecetedResult, aspect.getBundleSet(), RequestMode.Dev, "");
	}
	
	private void filterAndAssert(String input, String expectedOutput, BundleSet bundleSet, RequestMode opMode, String locale) throws Exception
	{
		StringWriter writer = new StringWriter();
		TagPluginUtility.filterContent(input, bundleSet, writer, opMode, new Locale(locale), brjs.getAppVersionGenerator().getDevVersion());
		assertEquals(expectedOutput, writer.toString());
	}
	
	private void filterAndAssertMapReturned(String input, Map<String,Map<String,String>> expectedResult, BundleSet bundleSet, RequestMode opMode, String locale) throws Exception
	{
		 Map<String,Map<String,String>> actualResult = TagPluginUtility.getUsedTagsAndAttributes(input, bundleSet, opMode, new Locale(locale));
		assertEquals(expectedResult, actualResult);
	}
}
