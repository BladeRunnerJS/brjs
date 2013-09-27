package com.caplin.cutlass.filter.bundlerfilter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.caplin.cutlass.filter.bundlerfilter.token.I18nBundleTokenProcessor;

public class I18nBundlerTokenProcessorTest
{
	I18nBundleTokenProcessor processor = new I18nBundleTokenProcessor();
	private HttpServletRequest request;
	
	@Test
	public void includingTokenReturnsCorrectBundle() throws TokenProcessorException
	{
		request = mock(HttpServletRequest.class);
		when(request.getLocale()).thenReturn(new Locale("en", "GB"));
		Map<String, String> attributes = new HashMap<String, String>();
		String content = processor.process("en_GB", "", attributes);
	
		String expected = "<script type=\"text/javascript\" src=\"i18n/en_i18n.bundle\"></script>\n<script type=\"text/javascript\" src=\"i18n/en_GB_i18n.bundle\"></script>\n";
		
		assertEquals(expected, content);
	}
}
