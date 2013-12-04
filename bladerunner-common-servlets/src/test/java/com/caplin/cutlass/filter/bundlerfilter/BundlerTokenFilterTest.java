package com.caplin.cutlass.filter.bundlerfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.sinbin.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;
import org.bladerunnerjs.model.utility.ServerUtility;
import com.caplin.cutlass.ServletModelAccessor;
import com.caplin.cutlass.conf.AppConf;
import com.caplin.cutlass.filter.bundlerfilter.token.CSSBundleTokenProcessor;
import com.caplin.cutlass.filter.bundlerfilter.token.JSBundleTokenProcessor;
import com.caplin.cutlass.request.LocaleHelper;

public class BundlerTokenFilterTest
{
	private static final int PORT = ServerUtility.getTestPort();
	
	private BundlerTokenProcessor processor;
	private HttpServletRequest request;
	private static final String LOCALE_LANGUAGE = "en";
	private static final String LOCALE_COUNTRY = "GB";
	private HttpClient httpclient;
	private Server appServer;
	private BufferedReader response;
	private Vector<Locale> locales;
	private AppConf appConf;

	@Before
	public void setup() throws IOException
	{
		ServletModelAccessor.reset();
		
		processor = new BundlerTokenProcessor();
		processor.addTokenProcessor(CutlassConfig.CSS_BUNDLE_TOKEN, new CSSBundleTokenProcessor());
		processor.addTokenProcessor(CutlassConfig.JS_BUNDLE_TOKEN, new JSBundleTokenProcessor());
		locales = new Vector<Locale>();
		appConf = new AppConf("appx", "en_GB,de_DE");
	}

	@After
	public void tearDown() throws Exception
	{
		if (appServer != null && appServer.isStarted())
		{
			appServer.stop();
		}

		if (httpclient != null)
		{
			httpclient.getConnectionManager().shutdown();
		}
	}

	private void setupHttpRequest(String content) throws IOException
	{
		setupHttpRequest(content,null);
	}
	
	private void setupHttpRequest(String content, Cookie[] cookies) throws IOException
	{
		StringReader dummyReader = new StringReader(content);
		request = mock(HttpServletRequest.class);
		response = new BufferedReader(dummyReader);

		locales.add( new Locale(LOCALE_LANGUAGE, LOCALE_COUNTRY) );
		when(request.getLocales()).thenReturn(locales.elements());
		when(request.getCookies()).thenReturn(cookies);
		when(request.getHeader("User-Agent")).thenReturn("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");
	}

	@Test
	public void testTokensReplacedCorrectly() throws IOException, TokenProcessorException
	{
		String content = "<html><head>" + "<@css.bundle theme=\"noir\"@/>" + "<@css.bundle alternateTheme=\"pastel\"@/>" + "<@js.bundle@/>" + "</head><body></body></html>";

		setupHttpRequest(content);
		StringBuffer buffer = processor.replaceTokens(appConf, request, response);

		String includes = buffer.toString();

		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_en_GB_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_ie7_css.bundle\"/>"));
		
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_en_GB_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_ie7_css.bundle\"/>"));

		assertTrue(includes.contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_en_GB_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_ie7_css.bundle\"/>"));

		assertTrue(includes.contains("<script type=\"text/javascript\" src=\"js/js.bundle\"></script>"));
	}
	
	@Test
	public void testSdkThemeIsOnlyIncludedOnceWhenRequesForMainAndAlertnateThemesAreUsed() throws IOException, TokenProcessorException
	{
		String content = "<html><head>" + "<@css.bundle theme=\"noir\"@/>" + "<@css.bundle alternateTheme=\"pastel\"@/>" + "<@js.bundle@/>" + "</head><body></body></html>";

		setupHttpRequest(content);
		StringBuffer buffer = processor.replaceTokens(appConf, request, response);
		String includes = buffer.toString();
		
		includes = includes.replaceFirst("<link rel=\"stylesheet\" href=\"css/cutlass-sdk_css.bundle\"/>", "");
		includes = includes.replaceFirst("<link rel=\"stylesheet\" href=\"css/cutlass-sdk_en_GB_css.bundle\"/>","");
		includes = includes.replaceFirst("<link rel=\"stylesheet\" href=\"css/cutlass-sdk_ie7_css.bundle\"/>","");
		
		assertFalse(includes.contains("<link rel=\"stylesheet\" href=\"css/cutlass-sdk_css.bundle\"/>"));
		assertFalse(includes.contains("<link rel=\"stylesheet\" href=\"css/cutlass-sdk_en_GB_css.bundle\"/>"));
		assertFalse(includes.contains("<link rel=\"stylesheet\" href=\"css/cutlass-sdk_ie7_css.bundle\"/>"));
	}

	@Test
	public void testVersionTokenDoesNotInterfereWithProcessing() throws TokenProcessorException, IOException
	{
		String content = "<html><head>" + "<@css.bundle theme=\"noir\"@/>" + "<@css.bundle alternateTheme=\"pastel\"@/>" + "<@js.bundle@/>" + "</head><base href=\"@APP.VERSION@\" /><body></body></html>";

		setupHttpRequest(content);
		StringBuffer buffer = processor.replaceTokens(appConf, request, response);

		assertTrue(buffer.toString().contains("@APP.VERSION@"));
		assertTrue(buffer.toString().contains("<link rel=\"stylesheet\" href=\"css/common_css.bundle\"/>"));
	}

	@Test
	public void testContentBeforeAndAfterReplacementStillExists() throws TokenProcessorException, IOException
	{
		String content = "<html><head>" + "<@css.bundle theme=\"noir\"@/>" + "<@css.bundle alternateTheme=\"pastel\"@/>" + "<@js.bundle@/>" + "</head><body></body></html>";

		setupHttpRequest(content);
		StringBuffer buffer = processor.replaceTokens(appConf, request, response);

		assertTrue(buffer.toString().startsWith("<html><head>"));
		assertTrue(buffer.toString().endsWith("</head><body></body></html>"));
	}

	@Test
	public void testSpacesBetweenAttributesIsProcessedCorrectly() throws IOException, TokenProcessorException
	{
		String content = "<@css.bundle	  alternateTheme = \"pastel\"@/>";

		setupHttpRequest(content);
		StringBuffer buffer = processor.replaceTokens(appConf, request, response);

		assertTrue(buffer.toString().contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_css.bundle\"/>"));
		assertTrue(buffer.toString().contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_en_GB_css.bundle\"/>"));
	}

	@Test
	public void testSpacesBetweenTagsIsProcessedCorrectly() throws IOException, TokenProcessorException
	{
		String content = "<@css.bundle alternateTheme = \"pastel\"@	  />";

		setupHttpRequest(content);
		StringBuffer buffer = processor.replaceTokens(appConf, request, response);

		assertTrue(buffer.toString().contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_css.bundle\"/>"));
		assertTrue(buffer.toString().contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_en_GB_css.bundle\"/>"));
	}

	@Test
	public void testMultiLineTagsAreProcessedCorrectly() throws IOException, TokenProcessorException
	{
		String content = "<@css.bundle\n alternateTheme\n =\n \"pastel\"@\n	  />";

		setupHttpRequest(content);
		StringBuffer buffer = processor.replaceTokens(appConf, request, response);

		assertTrue(buffer.toString().contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_css.bundle\"/>"));
		assertTrue(buffer.toString().contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_en_GB_css.bundle\"/>"));
	}

	@Test
	public void testIndexHtmlRequestIsProcessedCorrectly() throws Exception
	{
		String thisTestRoot = "src/test/resources/bundler-token";
		File tempSdkInstall = FileUtility.createTemporarySdkInstall(new File(thisTestRoot)).getParentFile();
		
		httpclient = new DefaultHttpClient();
		appServer = createServer(PORT, "/", new File(tempSdkInstall, "apps/app1").getPath());
		
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/main-aspect/index.html");
		String content = responseMap.get("responseText");
		assertEquals("200", responseMap.get("responseCode"));
		
		assertTrue(content.contains("<link rel=\"stylesheet\" href=\"css/common_css.bundle\"/>"));
		assertTrue(content.contains("<link rel=\"stylesheet\" href=\"css/common_en_GB_css.bundle\"/>"));
		assertTrue(content.contains("<link rel=\"stylesheet\" href=\"css/common_ie8_css.bundle\"/>"));

		assertTrue(content.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_css.bundle\"/>"));
		assertTrue(content.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_en_GB_css.bundle\"/>"));
		assertTrue(content.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_ie8_css.bundle\"/>"));

		assertTrue(content.contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_css.bundle\"/>"));
		assertTrue(content.contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_en_GB_css.bundle\"/>"));
		assertTrue(content.contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_ie8_css.bundle\"/>"));

		assertTrue(content.contains("<script type=\"text/javascript\" src=\"js/js.bundle\"></script>"));
	}

	@Test
	public void testIndexHtmlThatDoesNotExistDoesNotGetProcessed() throws Exception
	{
		httpclient = new DefaultHttpClient();
		File tempSdkInstall = FileUtility.createTemporarySdkInstall(new File("src/test/resources/bundler-token")).getParentFile();
		appServer = createServer(PORT, "/", new File(tempSdkInstall, "apps/app1").getPath());

		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/mobie-aspect/index.html");
		assertEquals("404", responseMap.get("responseCode"));
	}

	@Test
	public void testNonIndexHtmlRequestIsNotProcessed() throws Exception
	{
		httpclient = new DefaultHttpClient();
		File tempSdkInstall = FileUtility.createTemporarySdkInstall(new File("src/test/resources/bundler-token")).getParentFile();
		appServer = createServer(PORT, "/", new File(tempSdkInstall, "apps/app1").getPath());

		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/main-aspect/file.html");
		assertEquals("200", responseMap.get("responseCode"));
		String content = responseMap.get("responseText");
		assertTrue(content.contains("<@css.bundle theme=\"noir\"@ />"));
	}
	
	@Test(expected = TokenProcessorException.class) 
	public void testTokenProcessorExceptionIsThrownForIncorrectToken() throws Exception
	{
		String content = "<html><head>" + "<@css.bundl theme=\"noir\"@/>" + "<@css.bundle alternateTheme=\"pastel\"@/>" + "<@js.bundle@/>" + "</head><body></body></html>";
		
		setupHttpRequest(content);
		processor.replaceTokens(appConf, request, response);
	}
	
	@Test
	public void testLocaleCookieIsUsedIfPresent() throws IOException, TokenProcessorException
	{
		String content = "<html><head>" + "<@css.bundle theme=\"noir\"@/>" + "<@css.bundle alternateTheme=\"pastel\"@/>" + "<@js.bundle@/>" + "</head><body></body></html>";

		Cookie[] cookies = new Cookie[]{
				new Cookie("dummy.cookie","abc"),
				null,
				new Cookie(LocaleHelper.LOCALE_COOKIE_NAME, "de_DE"),
				new Cookie("another.cookie","1234")};
		
		setupHttpRequest(content, cookies);
		StringBuffer buffer = processor.replaceTokens(appConf, request, response);

		String includes = buffer.toString();
		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_de_DE_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_ie7_css.bundle\"/>"));

		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_de_DE_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_ie7_css.bundle\"/>"));

		assertTrue(includes.contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_de_DE_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"alternate stylesheet\" title=\"pastel\" href=\"css/pastel_ie7_css.bundle\"/>"));

		assertTrue(includes.contains("<script type=\"text/javascript\" src=\"js/js.bundle\"></script>"));
	}
	
	@Test
	public void testLocaleCanBeLanguageOnly() throws IOException, TokenProcessorException
	{
		String content = "<html><head>" + "<@css.bundle theme=\"noir\"@/>" +  "</head><body></body></html>";

		setupHttpRequest(content);
		locales.removeAllElements();
		locales.add( new Locale(LOCALE_LANGUAGE) );
		when(request.getLocales()).thenReturn(locales.elements());
		
		appConf.locales = LOCALE_LANGUAGE; 
		
		StringBuffer buffer = processor.replaceTokens(appConf, request, response);

		String includes = buffer.toString();

		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_en_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_ie7_css.bundle\"/>"));
		
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_en_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_ie7_css.bundle\"/>"));
	}
	@Test
	public void testCssRequestForBothCountryAndLanguageAreMade() throws IOException, TokenProcessorException
	{
		String content = "<html><head>" + "<@css.bundle theme=\"noir\"@/>" +  "</head><body></body></html>";
		
		setupHttpRequest(content);
		
		StringBuffer buffer = processor.replaceTokens(appConf, request, response);
		String includes = buffer.toString();
		
		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_en_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_en_GB_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" href=\"css/common_ie7_css.bundle\"/>"));
		
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_en_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_en_GB_css.bundle\"/>"));
		assertTrue(includes.contains("<link rel=\"stylesheet\" title=\"noir\" href=\"css/noir_ie7_css.bundle\"/>"));
	}

	private Map<String, String> makeRequest(String url) throws ClientProtocolException, IOException
	{
		Map<String, String> responseMap = new HashMap<String, String>();
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("Accept-Language", "en-GB");
		httpget.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0)");

		HttpResponse response = httpclient.execute(httpget);
		responseMap.put("responseCode", Integer.toString(response.getStatusLine().getStatusCode()));
		responseMap.put("responseText", EntityUtils.toString(response.getEntity()));
		String contentType = (ContentType.get(response.getEntity()) != null) ? ContentType.get(response.getEntity()).getMimeType().toString() : "";
		responseMap.put("responseContentType", contentType);

		return responseMap;
	}

	private Server createServer(int port, String contextPath, String resourceBase) throws Exception
	{
		Server appServer = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(contextPath);
		context.setResourceBase(resourceBase);
		context.addServlet(DefaultServlet.class, "/");
		context.addFilter(new FilterHolder(new BundlerTokenFilter()), "/*", null);
		appServer.setHandler(context);
		appServer.start();

		return appServer;
	}
}
