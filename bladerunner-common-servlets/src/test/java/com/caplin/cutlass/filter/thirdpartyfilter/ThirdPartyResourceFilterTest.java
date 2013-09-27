package com.caplin.cutlass.filter.thirdpartyfilter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.utility.ServerUtility;

public class ThirdPartyResourceFilterTest
{
	private static final int PORT = ServerUtility.getTestPort();
	private static final String APP_LOCATION = "src/test/resources/thirdpartyfilter/app1";
		
	private Server appServer;
	private HttpClient httpclient;

	@Before
	public void setup() throws Exception
	{
		httpclient = new DefaultHttpClient();
		appServer = createServer(PORT, "/app1", APP_LOCATION);
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
	public void testThirdPartyBundleEndingIsAppendedToThirdPartyLibraryResourceRequest() throws Exception
	{
		String requestUrl = "http://localhost:"+PORT+"/app1/thirdparty-libraries/lib1/lib1-resource.txt";
		
		HttpGet httpget = new HttpGet(requestUrl);
		HttpResponse response = httpclient.execute(httpget);
		
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals("Content of lib1-resource.txt_thirdparty.bundle", getResponseBody(response));
	}
	
	@Test
	public void testNonThirdPartyLibraryResourceRequestIsUnchanged() throws Exception
	{
		String requestUrl = "http://localhost:"+PORT+"/app1/default-aspect/index.html";
		
		HttpGet httpget = new HttpGet(requestUrl);
		HttpResponse response = httpclient.execute(httpget);
		
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals("Content of index.html", getResponseBody(response));
	}
	
	private String getResponseBody(HttpResponse response) throws ParseException, IOException
	{
		return EntityUtils.toString(response.getEntity());
	}

	private Server createServer(int port, String contextPath, String resourceBase) throws Exception
	{
		Server appServer = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

		context.setContextPath(contextPath);
		context.setResourceBase(resourceBase);
		context.addServlet(DefaultServlet.class, "/*");
		context.addFilter(new FilterHolder(new ThirdPartyResourceFilter()), "/*", null);
		appServer.setHandler(context);
		appServer.start();
		
		return appServer;
	}
}
