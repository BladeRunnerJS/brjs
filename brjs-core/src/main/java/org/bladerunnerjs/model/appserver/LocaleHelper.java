package org.bladerunnerjs.model.appserver;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.exception.ConfigException;

public class LocaleHelper
{
	// TODO: this should probabally be configurable in app.conf
	public static final String LOCALE_COOKIE_NAME = "BRJS.LOCALE";
	
	public static String getLocaleFromRequest(App app, HttpServletRequest request) throws ConfigException
	{	
		AppConf appConf = app.appConf();
		
		if (appConf.getLocales().equals(""))
		{
			throw new ConfigException("No locales have been defined for the app '"+app.getName()+"', an empty locale will be used.");
		}
		
		String[] supportedLocales = appConf.getLocales().split(",");
		supportedLocales = StringUtils.stripAll(supportedLocales);
		
		String cookieLocale = getLocaleFromCookie(request, Arrays.asList(supportedLocales));
		List<String> headerAcceptLanguageLocales = getAcceptLanguageLocalesFromRequest(request);
		String headerLocale = getFirstMatchingLocaleOrDefaultLocale(headerAcceptLanguageLocales, Arrays.asList(supportedLocales) );
		
		return (!cookieLocale.equals("")) ? cookieLocale : headerLocale;
	}
	
	public static String getLanguageFromLocale(String locale)
	{
		return locale.split("_")[0];
	}
	
	private static String getFirstMatchingLocaleOrDefaultLocale(List<String> requestedLocales, List<String> supportedLocales)
	{
		for (String requestedLocale : requestedLocales)
		{
			if (supportedLocales.contains(requestedLocale))
			{
				return requestedLocale;
			}
		}
		return supportedLocales.get(0);
	}
	
	@SuppressWarnings("unchecked")
	private static List<String> getAcceptLanguageLocalesFromRequest(HttpServletRequest request)
	{
		List<String> localeList = new LinkedList<String>();
		Enumeration<Locale> locales = request.getLocales();
		while (locales.hasMoreElements())
		{
			localeList.add(locales.nextElement().toString());
		}
		return localeList;
	}
	
	private static String getLocaleFromCookie(HttpServletRequest request, List<String> supportedLocales)
	{
		Cookie[] cookies = request.getCookies();
		String localeCookieValue = getCookieValue(cookies, LOCALE_COOKIE_NAME);
		if (supportedLocales.contains(localeCookieValue))
		{
			return localeCookieValue;
		}
		return "";
	}
	
	private static String getCookieValue(Cookie[] cookies, String cookieName)
	{
		if (cookies == null)
		{
			return "";
		}
		for (Cookie c : cookies)
		{
			if (c != null && c.getName().equals(cookieName))
			{
				return c.getValue();
			}
		}
		return "";
	}
	
}
