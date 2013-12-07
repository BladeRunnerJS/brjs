package com.caplin.cutlass.filter.production;

import static org.junit.Assert.assertEquals;

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
import org.bladerunnerjs.utility.ServerUtility;

public class ValidRequestForBundledResourceFilterTest {

	private ValidRequestForBundledResourceFilter filter;
	private Server appServer;
	private DefaultHttpClient httpclient;
	private String baseUrl;
	private int PORT = ServerUtility.getTestPort();
	
	@Before
	public void setup() throws Exception
	{
		this.filter = new ValidRequestForBundledResourceFilter();
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
	public void testNonExistingCssBundleReturnsStatusCode200() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet(baseUrl + "/app1/main-aspect/css/sometheme_ie6_css.bundle"));
		assertEquals(200, response.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testNonExistingi18nBundleReturnsStatusCode200() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet(baseUrl + "/app1/main-aspect/i18n/en_GB_i18n.bundle"));
		assertEquals(200, response.getStatusLine().getStatusCode());
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
	
	@Test
	public void testGetBundlePathFromSectionRootForValidSectionPathReturnsValidPath() throws Exception
	{
		ValidRequestForBundledResourceFilter filter = new ValidRequestForBundledResourceFilter();
		assertEquals("css/common_ie6_css.bundle", filter.getBundlePathFromSectionRoot("/app1/main-aspect/css/common_ie6_css.bundle"));
	}
	
	@Test
	public void testGetBundlePathFromSectionRootForNonSectionPathReturnsEmptyString() throws Exception
	{
		ValidRequestForBundledResourceFilter filter = new ValidRequestForBundledResourceFilter();
		assertEquals("", filter.getBundlePathFromSectionRoot("/app1/main/css/common_ie6_css.bundle"));
	}

	// TODO Write more unit tests
}
