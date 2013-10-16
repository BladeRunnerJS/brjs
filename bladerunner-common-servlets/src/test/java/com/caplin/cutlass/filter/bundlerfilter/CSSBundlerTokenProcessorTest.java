package com.caplin.cutlass.filter.bundlerfilter;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.caplin.cutlass.filter.bundlerfilter.token.CSSBundleTokenProcessor;

public class CSSBundlerTokenProcessorTest
{
	CSSBundleTokenProcessor processor = new CSSBundleTokenProcessor();
	
	@Test(expected=TokenProcessorException.class)
	public void addingCssBundleWithNoAttributeThrowsException() throws TokenProcessorException
	{
		Map<String, String> attributes = new HashMap<String, String>();
		processor.process("en_GB", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)", attributes);
	}
	
	@Test(expected=TokenProcessorException.class)
	public void addingCssBundleWithBadAttributeThrowsException() throws TokenProcessorException
	{
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("them", "noir");
		processor.process("en_GB", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)", attributes);
	}
	
	@Test(expected=TokenProcessorException.class)
	public void addingCssBundleWithEmptyThemeThrowsException() throws TokenProcessorException
	{
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("theme", "");
		processor.process("en_GB", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)", attributes);
	}
	
	@Test(expected=TokenProcessorException.class)
	public void addingCssBundleWithThemeAndAlternativeThrowsException() throws TokenProcessorException
	{
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("theme", "noir");
		attributes.put("alternateTheme", "pastel");
		processor.process("en_GB", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)", attributes);
	}
	
	@Test
	public void undeterminedBrowserDoesNotThrowExceptionAndDoesNotIncludeBrowserCssInclude() throws TokenProcessorException
	{
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("theme", "noir");
		String content = processor.process("en_GB", "", attributes);
	
		String expected = 	"<link rel=\"stylesheet\" href=\"css/common_css.bundle\"/>\n" + 
							"<link rel=\"stylesheet\" href=\"css/common_en_css.bundle\"/>\n" + 
							"<link rel=\"stylesheet\" href=\"css/common_en_GB_css.bundle\"/>\n" + 
							"<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_css.bundle\"/>\n" + 
							"<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_en_css.bundle\"/>\n" +
							"<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_en_GB_css.bundle\"/>\n";
		assertEquals(expected, content);
	}
}
