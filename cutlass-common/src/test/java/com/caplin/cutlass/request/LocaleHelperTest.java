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
		setSupportedLocales("en_GB");
		addPreferedLocale("en_GB");
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
		assertEquals("en_GB", LocaleHelper.getLocaleFromRequest(appConf, request));
	}
	
	@Test
	public void testGetLocaleFromRequestHeaderWithMultipleLocales() throws Exception
	{
		setSupportedLocales("en_GB");
		addPreferedLocale("en_US");
		addPreferedLocale("en_GB");
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
		assertEquals("en_GB", LocaleHelper.getLocaleFromRequest(appConf, request));
	}
	
	@Test
	public void testGetLocaleFromRequestHeaderWithMultipleLocalesWhereLastLocaleMatches() throws Exception
	{
		setSupportedLocales("en_GB");
		addPreferedLocale("en_GB");
		addPreferedLocale("en_US");
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
		assertEquals("en_GB", LocaleHelper.getLocaleFromRequest(appConf, request));
	}
	
	@Test
	public void testGetLocaleWhenLocaleDoesntMatchAnySupported() throws Exception
	{
		setSupportedLocales("en_GB");
		addPreferedLocale("de_DE");
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
		assertEquals("en_GB", LocaleHelper.getLocaleFromRequest(appConf, request));
	}
	
	@Test
	public void testMultipleSupportedLocalesAndMultipleRequestedLocales() throws Exception
	{
		setSupportedLocales("en_GB,de_DE,en,de");
		addPreferedLocale("de_DE");
		addPreferedLocale("de");
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
		assertEquals("de_DE", LocaleHelper.getLocaleFromRequest(appConf, request));
	}
	
	@Test
	public void testMultipleSupportedLocalesAndMultipleRequestedLocalesWithLanguageOnlyMatching() throws Exception
	{
		setSupportedLocales("en_GB,en,de");
		addPreferedLocale("de_DE");
		addPreferedLocale("de");
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
		assertEquals("de", LocaleHelper.getLocaleFromRequest(appConf, request));
	}

	@Test
	public void testLocaleCanBeOverriddenByCookie() throws Exception
	{
		setSupportedLocales("en_GB,en,de_DE,de");
		addPreferedLocale("en_GB");
		addPreferedLocale("en");
		
		Cookie[] cookies = new Cookie[3];
		cookies[0] = new Cookie("some-cookie", "a-value");
		cookies[1] = new Cookie("another-cookie", "a-value");
		cookies[2] = new Cookie(LocaleHelper.LOCALE_COOKIE_NAME, "de_DE");
		
		when(request.getCookies()).thenReturn( cookies );
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
		assertEquals("de_DE", LocaleHelper.getLocaleFromRequest(appConf, request));
	}
	
	@Test
	public void testCookieLocaleCantForceAnUnsupportedLocale() throws Exception
	{
		setSupportedLocales("en_GB,en,de_DE,de");
		addPreferedLocale("de_DE");
		addPreferedLocale("de");
		
		Cookie[] cookies = new Cookie[3];
		cookies[0] = new Cookie("some-cookie", "a-value");
		cookies[1] = new Cookie("another-cookie", "a-value");
		cookies[2] = new Cookie(LocaleHelper.LOCALE_COOKIE_NAME, "zh_CN");
		
		when(request.getCookies()).thenReturn( cookies );
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
		assertEquals("de_DE", LocaleHelper.getLocaleFromRequest(appConf, request));
	}
	
	@Test
	public void getLanguageFromLocale()
	{
		String language = LocaleHelper.getLanguageFromLocale("en_GB");
		assertEquals("en", language);
	}
	
	
	private void setSupportedLocales(String locales)
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

}
