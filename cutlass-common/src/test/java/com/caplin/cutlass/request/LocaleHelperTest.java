package com.caplin.cutlass.request;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;

import org.bladerunnerjs.model.exception.ConfigException;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.conf.AppConf;
import com.esotericsoftware.yamlbeans.YamlException;

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
		givenSupportedLocales("en_GB");
		addPreferedLocale("en_GB");
		whenRequestMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void testGetLocaleFromRequestHeaderWithMultipleLocales() throws Exception
	{
		givenSupportedLocales("en_GB");
		addPreferedLocale("en_US");
		addPreferedLocale("en_GB");
		whenRequestMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void testGetLocaleFromRequestHeaderWithMultipleLocalesWhereLastLocaleMatches() throws Exception
	{
		givenSupportedLocales("en_GB");
		addPreferedLocale("en_GB");
		addPreferedLocale("en_US");
		whenRequestMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void testGetLocaleWhenLocaleDoesntMatchAnySupported() throws Exception
	{
		givenSupportedLocales("en_GB");
		addPreferedLocale("de_DE");
		whenRequestMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void testMultipleSupportedLocalesAndMultipleRequestedLocales() throws Exception
	{
		givenSupportedLocales("en_GB,de_DE,en,de");
		addPreferedLocale("de_DE");
		addPreferedLocale("de");
		whenRequestMade();
		thenLocaleProvidedIs("de_DE");
	}
	
	@Test
	public void testMultipleSupportedLocalesAndMultipleRequestedLocalesWithLanguageOnlyMatching() throws Exception
	{
		givenSupportedLocales("en_GB,en,de");
		addPreferedLocale("de_DE");
		addPreferedLocale("de");
		whenRequestMade();
		thenLocaleProvidedIs("de");
	}

	@Test
	public void testLocaleCanBeOverriddenByCookie() throws Exception
	{
		givenSupportedLocales("en_GB,en,de_DE,de");
		addPreferedLocale("en_GB");
		addPreferedLocale("en");
		
		Cookie[] cookies = new Cookie[3];
		cookies[0] = new Cookie("some-cookie", "a-value");
		cookies[1] = new Cookie("another-cookie", "a-value");
		cookies[2] = new Cookie(LocaleHelper.LOCALE_COOKIE_NAME, "de_DE");
		
		when(request.getCookies()).thenReturn( cookies );
		whenRequestMade();
		thenLocaleProvidedIs("de_DE");
	}
	
	@Test
	public void testCookieLocaleCantForceAnUnsupportedLocale() throws Exception
	{
		givenSupportedLocales("en_GB,en,de_DE,de");
		addPreferedLocale("de_DE");
		addPreferedLocale("de");
		
		Cookie[] cookies = new Cookie[3];
		cookies[0] = new Cookie("some-cookie", "a-value");
		cookies[1] = new Cookie("another-cookie", "a-value");
		cookies[2] = new Cookie(LocaleHelper.LOCALE_COOKIE_NAME, "zh_CN");
		
		when(request.getCookies()).thenReturn( cookies );
		whenRequestMade();
		thenLocaleProvidedIs("de_DE");
	}
	
	@Test
	public void getLanguageFromLocale()
	{
		String language = LocaleHelper.getLanguageFromLocale("en_GB");
		assertEquals("en", language);
	}
	
	private void givenSupportedLocales(String locales)
	{
		appConf.locales = locales;
	}
	
	private void addPreferedLocale(String locale)
	{
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
	
	private void whenRequestMade() {
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
	}
	
	private void thenLocaleProvidedIs(String expectedLocale) throws Exception {
		assertEquals(expectedLocale, LocaleHelper.getLocaleFromRequest(appConf, request));
	}
}
