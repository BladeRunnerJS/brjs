package com.caplin.cutlass.bundler.html;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.bladerunnerjs.model.sinbin.AppMetaData;

public class ValidRequestStringsHtmlBundlerTest {
	
	@Test
	public void testHtmlBundlerReturnsExpectedValidRequestStrings() throws Exception
	{
		HtmlBundler htmlBundler = new HtmlBundler();
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getLocales()).thenReturn(Arrays.asList("de_DE"));
		when(metaData.getBrowsers()).thenReturn(Arrays.asList("ie6"));
		when(metaData.getThemes()).thenReturn(Arrays.asList("noir"));
		
		List<String> requests = htmlBundler.getValidRequestStrings(metaData);
		
		assertEquals(Arrays.asList("html.bundle"), requests);
	}

}
