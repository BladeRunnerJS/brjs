package com.caplin.cutlass.bundler.i18n;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.caplin.cutlass.AppMetaData;

public class I18nBundlerTest
{
	
	@Test
	public void testAppWithOneLocaleGetsValidRequestStrings() throws Exception
	{
		I18nBundler i18nBundler = new I18nBundler();
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getLocales()).thenReturn(Arrays.asList("en_GB"));
		
		List<String> requests = i18nBundler.getValidRequestStrings(metaData);
		
		assertEquals(Arrays.asList("i18n/en_GB_i18n.bundle"), requests);
	}
	
	@Test
	public void testAppWithNoLocaleGetsValidRequestStrings() throws Exception
	{
		I18nBundler i18nBundler = new I18nBundler();
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getLocales()).thenReturn(Collections.<String>emptyList());
		
		List<String> requests = i18nBundler.getValidRequestStrings(metaData);
		
		assertEquals(Collections.<String>emptyList(), requests);
	}

}
