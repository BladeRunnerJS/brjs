package com.caplin.cutlass.filter.bundlerfilter;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

public class BrowserCssHelper
{
	
	public static final String UNKNOWN_BROWSER = "unknown";
	
	/**
	 * Returns a string that can be used for a specific CSS file for the browser
	 * that made the request.
	 * 
	 * @param userAgentStr The User-Agent string from the browser request.
	 * @return The browser specific string that can be used to generate a CSS file
	 * name for the browser, for example "ie7" or "FF"
	 */
	public static String getBrowser(String userAgentStr)
	{
		if (userAgentStr == null)
		{
			return UNKNOWN_BROWSER;
		}
		
		UserAgentStringParser userAgentParser = UADetectorServiceFactory.getResourceModuleParser();
		ReadableUserAgent userAgent = userAgentParser.parse(userAgentStr);
		
		String browser = sanitizeBrowserName(userAgent.getName());
		if (browser.equals(""))
		{
			return UNKNOWN_BROWSER;
		}
		
		String browserVersion = userAgent.getVersionNumber().getMajor();
		String browserStr = browser + browserVersion;
		
		return browserStr;
	}
	
	private static String sanitizeBrowserName(String browserName) 
	{
		browserName = browserName.toLowerCase();
		
		// if browser name is 'mobile safari' return 'safari'
		browserName = browserName.replace("mobile", "").trim();
		return browserName;
	}
	
}
