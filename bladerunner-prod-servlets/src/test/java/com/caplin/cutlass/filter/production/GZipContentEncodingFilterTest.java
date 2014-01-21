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

import com.caplin.cutlass.ServletModelAccessor;

public class GZipContentEncodingFilterTest
{
	private static final String APP_DIR = "src/test/resources/cutlass-prod-filters/app1";
	
	private GZipContentEncodingFilter filter;
	private Server appServer;
	private DefaultHttpClient httpclient;
	private String baseUrl;
	
	private int PORT = ServerUtility.getTestPort();
	
	@Before
	public void setup() throws Exception
	{
		ServletModelAccessor.destroy();
		this.filter = new GZipContentEncodingFilter();
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
	public void testHtmlBundleContentEncodingIsGzip() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet(baseUrl + "/app1/main-aspect/html.bundle"));
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals("gzip", response.getEntity().getContentEncoding().getValue());
	}
	
	@Test
	public void testNonBundledResourceContentEncodingIsNotSet() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet(baseUrl + "/app1/main-aspect/index.html"));
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(null, response.getEntity().getContentEncoding());
	}
	
	@Test
	public void testBundledImageResourceContentEncodingIsNotSet() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet(baseUrl + "/app1/main-aspect/someImage.png_image.bundle"));
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(null, response.getEntity().getContentEncoding());
	}
	
	@Test
	public void test404ResponseContentEncodingIsNotSet() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet(baseUrl + "/app1/main-aspect/bla_bla_css.bundle"));
		assertEquals(404, response.getStatusLine().getStatusCode());
		assertEquals(null, response.getEntity().getContentEncoding());
	}
	
	private Server createServer() throws Exception
	{
		Server appServer = new Server(PORT);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/app1");
		context.setResourceBase(APP_DIR);
		context.addFilter(new FilterHolder(filter), "/*", null);
		context.addServlet(DefaultServlet.class, "/*");
		appServer.setHandler(context);
		appServer.start();
		
		return appServer;
	}
}
