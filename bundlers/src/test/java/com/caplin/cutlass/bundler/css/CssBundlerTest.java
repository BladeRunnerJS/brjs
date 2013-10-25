package com.caplin.cutlass.bundler.css;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import org.bladerunnerjs.model.sinbin.AppMetaData;

public class CssBundlerTest
{

	@Test
	public void testAppWithOneLocaleOneBrowserAndOneThemeGetsValidRequestStrings() throws Exception
	{
		CssBundler cssBundler = new CssBundler();
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getLocales()).thenReturn(Arrays.asList("de_DE"));
		when(metaData.getBrowsers()).thenReturn(Arrays.asList("ie6"));
		when(metaData.getThemes()).thenReturn(Arrays.asList("noir"));
		
		List<String> requests = cssBundler.getValidRequestStrings(metaData);
		
		assertEquals(Arrays.asList("css/noir_css.bundle", "css/noir_de_DE_css.bundle", "css/noir_ie6_css.bundle", "css/common_css.bundle"), requests);
	}
	
	@Test
	public void testAppWithTwoLocalesThreeBrowsersAndTwoThemesGetsValidRequestStrings() throws Exception
	{
		CssBundler cssBundler = new CssBundler();
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getLocales()).thenReturn(Arrays.asList("en_GB", "de_DE"));
		when(metaData.getBrowsers()).thenReturn(Arrays.asList("ie6", "ie7", "ff12"));
		when(metaData.getThemes()).thenReturn(Arrays.asList("noir", "pastel"));
		
		List<String> requests = cssBundler.getValidRequestStrings(metaData);
		
		assertEquals(Arrays.asList("css/noir_css.bundle",
									"css/noir_en_GB_css.bundle", "css/noir_de_DE_css.bundle",
									"css/noir_ie6_css.bundle", "css/noir_ie7_css.bundle", "css/noir_ff12_css.bundle",
									"css/pastel_css.bundle",
									"css/pastel_en_GB_css.bundle", "css/pastel_de_DE_css.bundle",
									"css/pastel_ie6_css.bundle", "css/pastel_ie7_css.bundle", "css/pastel_ff12_css.bundle", "css/common_css.bundle"), requests);
	}
	
	@Test
	public void testAppWithNoLocalesNoBrowsersAndTwoThemesGetsValidRequestStrings() throws Exception
	{
		CssBundler cssBundler = new CssBundler();
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getLocales()).thenReturn(Collections.<String> emptyList());
		when(metaData.getBrowsers()).thenReturn(Collections.<String> emptyList());
		when(metaData.getThemes()).thenReturn(Arrays.asList("noir", "pastel"));
		
		List<String> requests = cssBundler.getValidRequestStrings(metaData);
		
		assertEquals(Arrays.asList("css/noir_css.bundle","css/pastel_css.bundle", "css/common_css.bundle"), requests);
	}
	
	
	@Test
	public void testAppWithLanguageOnlyLocaleGetsValueRequestStrings() throws Exception
	{
		CssBundler cssBundler = new CssBundler();
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getLocales()).thenReturn(Arrays.asList("en_GB", "en", "de_DE", "de"));
		when(metaData.getBrowsers()).thenReturn(Arrays.asList("ie6", "ie7", "ff12"));
		when(metaData.getThemes()).thenReturn(Arrays.asList("noir", "pastel"));
		
		List<String> requests = cssBundler.getValidRequestStrings(metaData);
		
		assertEquals(Arrays.asList("css/noir_css.bundle",
									"css/noir_en_GB_css.bundle", "css/noir_en_css.bundle", "css/noir_de_DE_css.bundle", "css/noir_de_css.bundle",
									"css/noir_ie6_css.bundle", "css/noir_ie7_css.bundle", "css/noir_ff12_css.bundle",
									"css/pastel_css.bundle",
									"css/pastel_en_GB_css.bundle", "css/pastel_en_css.bundle", "css/pastel_de_DE_css.bundle", "css/pastel_de_css.bundle",
									"css/pastel_ie6_css.bundle", "css/pastel_ie7_css.bundle", "css/pastel_ff12_css.bundle", "css/common_css.bundle"), requests);
	}
	
}
