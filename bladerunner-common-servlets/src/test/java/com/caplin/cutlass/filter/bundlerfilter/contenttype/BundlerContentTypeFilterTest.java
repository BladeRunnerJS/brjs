package com.caplin.cutlass.filter.bundlerfilter.contenttype;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.utility.ServerUtility;
import com.caplin.cutlass.filter.bundlerfilter.contenttype.BundlerContentTypeFilter;

import static org.junit.Assert.assertEquals;

public class BundlerContentTypeFilterTest 
{

	private BundlerContentTypeFilter filter;
	private Server appServer;
	private DefaultHttpClient httpclient;
	private String baseUrl;
	private int PORT = ServerUtility.getTestPort();
	
	@Before
	public void setup() throws Exception
	{
		this.filter = new BundlerContentTypeFilter();
		this.baseUrl = "http://localhost:" + PORT;
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
	
	@Test
	public void testContentTypeForHtml() throws Exception {
		assertEquals("text/html", filter.getContentType(baseUrl + "/app1/bundles/html/html.bundle"));
	}
	
	@Test
	public void testContentTypeForXml() throws Exception {
		assertEquals("application/xml", filter.getContentType(baseUrl + "/app1/bundles/xml/xml.bundle"));
	}
	
	@Test
	public void testContentTypeForCss() throws Exception {
		assertEquals("text/css", filter.getContentType(baseUrl + "/css/noir_css.bundle"));
	}
	
	@Test
	public void testContentTypeForJs() throws Exception {
		assertEquals("text/javascript", filter.getContentType(baseUrl + "/app1/bundles/js/js.bundle"));
	}
	
	@Test
	public void testContentTypeForI18n() throws Exception {
		assertEquals("text/javascript", filter.getContentType(baseUrl + "/app1/bundles/i18n/en_GB_i18n.bundle"));
	}
	
	@Test
	public void testContentTypeForPngImage() throws Exception
	{
		assertEquals("image/png", filter.getContentType(baseUrl + "/images/theme_noir/image.png_image.bundle"));
	}
	
	@Test
	public void testContentTypeForJpegImage() throws Exception
	{
		assertEquals("image/jpeg", filter.getContentType(baseUrl + "/images/theme_noir/image.jpeg_image.bundle"));
	}
	
	@Test
	public void testContentTypeForGifImage() throws Exception
	{
		assertEquals("image/gif", filter.getContentType(baseUrl + "/images/theme_noir/image.gif_image.bundle"));
	}
	
	@Test
	public void testContentTypeForSVGImage() throws Exception
	{
		assertEquals("image/svg+xml", filter.getContentType(baseUrl + "/images/theme_noir/image.svg_image.bundle"));
	}
	
	@Test
	public void testContentTypeForIcoImage() throws Exception
	{
		assertEquals("image/vnd.microsoft.icon", filter.getContentType(baseUrl + "/images/theme_noir/image.ico_image.bundle"));
	}
	
	@Test
	public void testContentTypeForCurImage() throws Exception
	{
		assertEquals("image/vnd.microsoft.icon", filter.getContentType(baseUrl + "/images/theme_noir/image.cur_image.bundle"));
	}

	@Test
	public void testContentTypeForRootJpgImage() throws Exception
	{
		assertEquals("image/jpeg", filter.getContentType("image.jpg_image.bundle"));
	}
	
	@Test
	public void testContentTypeForMalformedImageRequest() throws Exception
	{
		assertEquals(null, filter.getContentType("image.bundle"));
	}
	
	@Test
	public void testContentTypeForThirdpartyBundles() throws Exception
	{
		assertEquals("text/javascript", filter.getContentType("/app1/thirdparty-libraries/someLib/file.js_thirdparty.bundle"));
		assertEquals("application/xml", filter.getContentType("/app1/thirdparty-libraries/someLib/file.xml_thirdparty.bundle"));
		assertEquals("text/html", filter.getContentType("/app1/thirdparty-libraries/someLib/file.html_thirdparty.bundle"));
		assertEquals("text/plain", filter.getContentType("/app1/thirdparty-libraries/someLib/file.txt_thirdparty.bundle"));
		assertEquals("image/png", filter.getContentType("/app1/thirdparty-libraries/someLib/file.png_thirdparty.bundle"));
		assertEquals("image/gif", filter.getContentType("/app1/thirdparty-libraries/someLib/file.gif_thirdparty.bundle"));
	}
	
	@Test
	public void testHtmlBundleContentTypeIsHtmlText() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet(baseUrl + "/app1/html/html.bundle"));
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals("text/html", response.getEntity().getContentType().getValue());
	}
	
	@Test
	public void testPngImageBundleContentTypeIsImagePng() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet(baseUrl + "/app1/images/theme-noir/someImage.png_image.bundle"));
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals("image/png", response.getEntity().getContentType().getValue());
	}
	
	private Server createServer() throws Exception
	{
		Server appServer = new Server(PORT);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/app1");
		context.setResourceBase("src/test/resources/BundlerContentTypeFilter/app1");
		context.addFilter(new FilterHolder(filter), "/*", null);
		context.addServlet(DefaultServlet.class, "/*");
		appServer.setHandler(context);
		appServer.start();
		
		return appServer;
	}
}
