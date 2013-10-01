package com.caplin.cutlass.request;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.conf.AppConf;
import com.google.common.base.Joiner;

public class LocaleHelperTest
{
	private HttpServletRequest request;
	private Vector<Locale> preferedLocales;
	private AppConf appConf;
	
	@Before
	public void setup()
	{
		request = mock(HttpServletRequest.class);
		preferedLocales = new Vector<Locale>();
		appConf = new AppConf("appx","");
	}
	
	@Test
	public void testGetLocaleFromRequestHeader() throws Exception
	{
		givenServerSupportsLocales("en_GB");
			andBrowserPrefersLocales("en_GB");
		whenRequestIsMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void testGetLocaleFromRequestHeaderWithMultipleLocales() throws Exception
	{
		givenServerSupportsLocales("en_GB");
			andBrowserPrefersLocales("en_US", "en_GB");
		whenRequestIsMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void testGetLocaleFromRequestHeaderWithMultipleLocalesWhereLastLocaleMatches() throws Exception
	{
		givenServerSupportsLocales("en_GB");
			andBrowserPrefersLocales("en_GB", "en_US");
		whenRequestIsMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void testGetLocaleWhenLocaleDoesntMatchAnySupported() throws Exception
	{
		givenServerSupportsLocales("en_GB");
			andBrowserPrefersLocales("de_DE");
		whenRequestIsMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void testMultipleSupportedLocalesAndMultipleRequestedLocales() throws Exception
	{
		givenServerSupportsLocales("en_GB", "de_DE", "en", "de");
			andBrowserPrefersLocales("de_DE", "de");
		whenRequestIsMade();
		thenLocaleProvidedIs("de_DE");
	}
	
	@Test
	public void testMultipleSupportedLocalesAndMultipleRequestedLocalesWithLanguageOnlyMatching() throws Exception
	{
		givenServerSupportsLocales("en_GB", "en", "de");
			andBrowserPrefersLocales("de_DE", "de");
		whenRequestIsMade();
		thenLocaleProvidedIs("de");
	}

	@Test
	public void testLocaleCanBeOverriddenByCookie() throws Exception
	{
		givenServerSupportsLocales("en_GB", "en", "de_DE", "de");
			andBrowserPrefersLocales("en_GB", "en");
			andBrowserHasCookies(
				new Cookie("some-cookie", "a-value"),
				new Cookie("another-cookie", "a-value"),
				new Cookie(LocaleHelper.LOCALE_COOKIE_NAME, "de_DE"));
		whenRequestIsMade();
		thenLocaleProvidedIs("de_DE");
	}
	
	@Test
	public void testCookieLocaleCantForceAnUnsupportedLocale() throws Exception
	{
		givenServerSupportsLocales("en_GB", "en", "de_DE", "de");
			andBrowserPrefersLocales("de_DE", "de");
			andBrowserHasCookies(
				new Cookie("some-cookie", "a-value"),
				new Cookie("another-cookie", "a-value"),
				new Cookie(LocaleHelper.LOCALE_COOKIE_NAME, "zh_CN"));
		whenRequestIsMade();
		thenLocaleProvidedIs("de_DE");
	}
	
	@Test
	public void getLanguageFromLocale()
	{
		String language = LocaleHelper.getLanguageFromLocale("en_GB");
		assertEquals("en", language);
	}
	
	private void givenServerSupportsLocales(String... locales)
	{
		appConf.locales = Joiner.on(",").join(locales);
	}
	
	private void andBrowserPrefersLocales(String... locales)
	{
		for(String locale : locales) {
			String[] localeSplit = locale.split("_");
			
			if (localeSplit.length == 1)
			{
				preferedLocales.add(new Locale(localeSplit[0]));
			}
			else
			{
				preferedLocales.add(new Locale(localeSplit[0],localeSplit[1]));
			}
		}
	}
	
	private void andBrowserHasCookies(Cookie... cookies) {
		when(request.getCookies()).thenReturn( cookies );
	}
	
	private void whenRequestIsMade() {
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
	}
	
	private void thenLocaleProvidedIs(String expectedLocale) throws Exception {
		assertEquals(expectedLocale, LocaleHelper.getLocaleFromRequest(appConf, request));
	}
}
