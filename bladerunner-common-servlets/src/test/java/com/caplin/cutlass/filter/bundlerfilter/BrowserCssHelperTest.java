package com.caplin.cutlass.filter.bundlerfilter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BrowserCssHelperTest
{
	
	/* example UA strings from http://www.useragentstring.com/ */
	
	@Test
	public void testInvalidOrUnknownStringsAreHandled()
	{
		assertEquals(BrowserCssHelper.UNKNOWN_BROWSER, BrowserCssHelper.getBrowser("abcd1234"));
		assertEquals(BrowserCssHelper.UNKNOWN_BROWSER, BrowserCssHelper.getBrowser(null));
	}
	
	/*#### IE UA Strings ####*/
	
	@Test
	public void testCorrectVersionIsReturnedForFutureIEVersions()
	{
		// these UA strings are speculative (04/2012) - included to check that we can detect future versions of IE
		assertEquals("ie99", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; MSIE 99.0; Windows NT 6.1; Trident/4.0; InfoPath.2; SV1; .NET CLR 2.0.50727; WOW64)"));	
		assertEquals("ie15", BrowserCssHelper.getBrowser("Mozilla/1.22 (compatible; MSIE 15.0; Windows 3.1)"));	
		assertEquals("ie14", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; MSIE 14.0; Windows NT 6.1; WOW64; Trident/6.0)"));	
		assertEquals("ie13", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; MSIE 13.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0"));
		assertEquals("ie12", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; MSIE 12.0; Windows NT 6.1; Trident/4.0; InfoPath.2; SV1; .NET CLR 2.0.50727; WOW64)"));	
	}
	
	@Test
	public void testCorrectVersionIsReturnedForIE10()
	{
		assertEquals("ie10", BrowserCssHelper.getBrowser("Mozilla/1.22 (compatible; MSIE 10.0; Windows 3.1)"));	
		assertEquals("ie10", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)"));	
		assertEquals("ie10", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0"));
		assertEquals("ie10", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/4.0; InfoPath.2; SV1; .NET CLR 2.0.50727; WOW64)"));	
	}
	
	@Test
	public void testCorrectVersionIsReturnedForIE9()
	{
		assertEquals("ie9", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 5.1)"));
		assertEquals("ie9", BrowserCssHelper.getBrowser("Mozilla/5.0 (Windows; U; MSIE 9.0; WIndows NT 9.0; en-US))"));
		assertEquals("ie9", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; Media Center PC 6.0; InfoPath.3; MS-RTC LM 8; Zune 4.7)"));
		assertEquals("ie9", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/4.0; FDM; MSIECrawler; Media Center PC 5.0)"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForIE8()
	{
		assertEquals("ie8", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1)"));
		assertEquals("ie8", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)"));
		assertEquals("ie8", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; Media Center PC 6.0; InfoPath.2; MS-RTC LM 8)"));
		assertEquals("ie8", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; msn OptimizedIE8;ZHCN)"));
		assertEquals("ie8", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; GTB6.5; QQDownload 534; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) ; SLCC2; .NET CLR 2.0.50727; Media Center PC 6.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729)"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForIE7()
	{
		assertEquals("ie7", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)"));
		assertEquals("ie7", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.2; .NET CLR 3.0.04506.30)"));
		assertEquals("ie7", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.1; .NET CLR 1.0.3705; Media Center PC 3.1; Alexa Toolbar; .NET CLR 1.1.4322; .NET CLR 2.0.50727)"));
		assertEquals("ie7", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.1; .NET CLR 1.1.4322; Alexa Toolbar; .NET CLR 2.0.50727)"));
		assertEquals("ie7", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 6.0; WOW64; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; c .NET CLR 3.0.04506; .NET CLR 3.5.30707; InfoPath.1; el-GR)"));
		assertEquals("ie7", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; MSIE 7.0; Windows 98; SpamBlockerUtility 6.3.91; SpamBlockerUtility 6.2.91; .NET CLR 4.1.89;GB)"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForIE6()
	{
		assertEquals("ie6", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 6.0; Windows CE; IEMobile"));
		assertEquals("ie6", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)"));
		assertEquals("ie6", BrowserCssHelper.getBrowser("Mozilla/4.0 (Compatible; Windows NT 5.1; MSIE 6.0) (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)"));
		assertEquals("ie6", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; U; MSIE 6.0; Windows NT 5.1) (Compatible; ; ; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)"));
	}
	

	/*#### Chrome UA Strings ####*/

	@Test
	public void testCorrectVersionIsReturnedForFutureChromeVersions()
	{
		// these UA strings are speculative (04/2012) - included to check that we can detect future versions of Chrome
		assertEquals("chrome99", BrowserCssHelper.getBrowser("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/99.0.1084.9 Safari/536.5"));
		assertEquals("chrome25", BrowserCssHelper.getBrowser("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/25.0.1084.9 Safari/536.5"));
		assertEquals("chrome24", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/24.0.1055.1 Safari/535.24"));
		assertEquals("chrome23", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/23.0.1036.7 Safari/535.20"));	
	}
	
	@Test
	public void testCorrectVersionIsReturnedForChrome20()
	{
		assertEquals("chrome20", BrowserCssHelper.getBrowser("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6"));
		assertEquals("chrome20", BrowserCssHelper.getBrowser("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1090.0 Safari/536.6"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForChrome19()
	{
		assertEquals("chrome19", BrowserCssHelper.getBrowser("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.9 Safari/536.5"));
		assertEquals("chrome19", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24"));
		assertEquals("chrome19", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/19.0.1036.7 Safari/535.20"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForChrome17()
	{
		assertEquals("chrome17", BrowserCssHelper.getBrowser("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.66 Safari/535.11"));
		assertEquals("chrome17", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.66 Safari/535.11"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForChrome16()
	{
		assertEquals("chrome16", BrowserCssHelper.getBrowser("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7ad-imcjapan-syosyaman-xkgi3lqg03!wgz"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForChrome10()
	{
		assertEquals("chrome10", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.0 Safari/534.16"));
		assertEquals("chrome10", BrowserCssHelper.getBrowser("Mozilla/5.0 (X11; U; Linux x86_64; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.11 Safari/534.16"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForChrome6()
	{
		assertEquals("chrome6", BrowserCssHelper.getBrowser("Mozilla/5.0 (X11; U; Linux x86_64; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.470.0 Safari/534.3"));
		assertEquals("chrome6", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.464.0 Safari/534.3"));
	}

	/*#### Firefox UA Strings ####*/
	
	@Test
	public void testCorrectVersionIsReturnedForFutureFirefoxVersions()
	{
		// these UA strings are speculative (04/2012) - included to check that we can detect future versions of Firefox
		assertEquals("firefox99", BrowserCssHelper.getBrowser("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20120427 Firefox/99.0a1"));	
		assertEquals("firefox25", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; Windows; U; Windows NT 6.2; WOW64; en-US; rv:12.0) Gecko/20120403211507 Firefox/25.0"));
		assertEquals("firefox24", BrowserCssHelper.getBrowser("Mozilla/5.0 (X11; U; FreeBSD i386; de-CH; rv:1.9.2.8) Gecko/20100729 Firefox/24.6.8"));
		assertEquals("firefox23", BrowserCssHelper.getBrowser("Mozilla/5.0 (X11; U; Linux MIPS32 1074Kf CPS QuadCore; en-US; rv:1.9.2.13) Gecko/20110103 Fedora/3.6.13-1.fc14 Firefox/23.6.13"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForFirefox15()
	{
		assertEquals("firefox15", BrowserCssHelper.getBrowser("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20120427 Firefox/15.0a1"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForFirefox12()
	{
		assertEquals("firefox12", BrowserCssHelper.getBrowser("Mozilla/5.0 (compatible; Windows; U; Windows NT 6.2; WOW64; en-US; rv:12.0) Gecko/20120403211507 Firefox/12.0"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForFirefox9()
	{
		assertEquals("firefox9", BrowserCssHelper.getBrowser("Mozilla/5.0 (Windows NT 6.2; rv:9.0.1) Gecko/20100101 Firefox/9.0.1"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForFirefox5()
	{
		assertEquals("firefox5", BrowserCssHelper.getBrowser("Mozilla/5.0 (X11; Linux x86_64; rv:5.0) Gecko/20100101 Firefox/5.0 FirePHP/0.5"));
		assertEquals("firefox5", BrowserCssHelper.getBrowser("Mozilla/5.0 (Windows NT 6.1; U; ru; rv:5.0.1.6) Gecko/20110501 Firefox/5.0.1 Firefox/5.0.1"));
		assertEquals("firefox5", BrowserCssHelper.getBrowser("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForFirefox3()
	{
		assertEquals("firefox3", BrowserCssHelper.getBrowser("Mozilla/5.0 (X11; U; FreeBSD i386; de-CH; rv:1.9.2.8) Gecko/20100729 Firefox/3.6.8"));
		assertEquals("firefox3", BrowserCssHelper.getBrowser("Mozilla/5.0 (X11; U; Linux MIPS32 1074Kf CPS QuadCore; en-US; rv:1.9.2.13) Gecko/20110103 Fedora/3.6.13-1.fc14 Firefox/3.6.13"));
	}

	/*#### Safari UA Strings ####*/
	
	@Test
	public void testCorrectVersionIsReturnedForFutureSafariVersions()
	{
		// these UA strings are speculative (04/2012) - included to check that we can detect future versions of Safari
		assertEquals("safari99", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/534.55.3 (KHTML, like Gecko) Version/99.1.3 Safari/534.53.10"));
		assertEquals("safari25", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/534.55.3 (KHTML, like Gecko) Version/25.1.3 Safari/534.53.10"));
		assertEquals("safari24", BrowserCssHelper.getBrowser("Mozilla/5.0 (iPhone; U; fr; CPU iPhone OS 4_2_1 like Mac OS X; fr) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/24.0.2 Mobile/8C148a Safari/6533.18.5"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForSafari5()
	{
		assertEquals("safari5", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/534.55.3 (KHTML, like Gecko) Version/5.1.3 Safari/534.53.10"));
		assertEquals("safari5", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_5_8; zh-cn) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27"));
		assertEquals("safari5", BrowserCssHelper.getBrowser("Mozilla/5.0 (iPhone; U; fr; CPU iPhone OS 4_2_1 like Mac OS X; fr) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148a Safari/6533.18.5"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForSafari4()
	{
		assertEquals("safari4", BrowserCssHelper.getBrowser("Mozilla/5.0 (Windows; U; Windows NT 5.0; en-en) AppleWebKit/533.16 (KHTML, like Gecko) Version/4.1 Safari/533.16"));
		assertEquals("safari4", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_2; de-at) AppleWebKit/531.21.8 (KHTML, like Gecko) Version/4.0.4 Safari/531.21.10"));
		assertEquals("safari4", BrowserCssHelper.getBrowser("Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_5_8; en-us) AppleWebKit/532.0+ (KHTML, like Gecko) Version/4.0.3 Safari/531.9.2009"));
	}

	/*#### Opera UA Strings ####*/
	
	@Test
	public void testCorrectVersionIsReturnedForFutureOperaVersions()
	{
		// these UA strings are speculative (04/2012) - included to check that we can detect future versions of Opera
		assertEquals("opera99", BrowserCssHelper.getBrowser("Opera/9.80 (S60; SymbOS; Opera Tablet/9174; U; en) Presto/2.7.81 Version/99.5"));
		assertEquals("opera25", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; ru) Opera 25.52"));
		assertEquals("opera24", BrowserCssHelper.getBrowser("Opera/9.80 (X11; Linux x86_64; U; pl) Presto/2.7.62 Version/24.00"));
		assertEquals("opera23", BrowserCssHelper.getBrowser("Opera/9.80 (S60; SymbOS; Opera Tablet/9174; U; en) Presto/2.7.81 Version/23.5"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForOpera12()
	{
		assertEquals("opera12", BrowserCssHelper.getBrowser("Opera/9.80 (Windows NT 6.1; U; es-ES) Presto/2.9.181 Version/12.00"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForOpera11()
	{
		assertEquals("opera11", BrowserCssHelper.getBrowser("Opera/9.80 (Windows NT 6.1; WOW64; U; pt) Presto/2.10.229 Version/11.62"));
		assertEquals("opera11", BrowserCssHelper.getBrowser("Opera/9.80 (X11; Linux i686; U; hu) Presto/2.9.168 Version/11.50"));
		assertEquals("opera11", BrowserCssHelper.getBrowser("Opera/9.80 (X11; Linux x86_64; U; pl) Presto/2.7.62 Version/11.00"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForOpera10()
	{
		assertEquals("opera10", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; en) Opera 10.62"));
		assertEquals("opera10", BrowserCssHelper.getBrowser("Opera/9.80 (S60; SymbOS; Opera Tablet/9174; U; en) Presto/2.7.81 Version/10.5"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForOpera9()
	{
		assertEquals("opera9", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; ru) Opera 9.52"));
	}	
	
	@Test
	public void testCorrectVersionIsReturnedForOpera8()
	{
		assertEquals("opera8", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; sv) Opera 8.51"));
	}
	
	@Test
	public void testCorrectVersionIsReturnedForOpera7()
	{
		assertEquals("opera7", BrowserCssHelper.getBrowser("Mozilla/4.0 (compatible; MSIE 5.23; Mac_PowerPC) Opera 7.54 [en]"));
	}
	
}
