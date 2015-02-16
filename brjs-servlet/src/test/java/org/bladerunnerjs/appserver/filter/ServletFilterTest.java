package org.bladerunnerjs.appserver.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;

@SuppressWarnings("deprecation")
public class ServletFilterTest {
	protected final int serverPort = new Random().nextInt(2000)+1000;
	protected HttpClient httpclient;
	
	@Before
	public void setUp() {
		httpclient = new DefaultHttpClient();
	}
	
	@After
	public void tearDown() {
		httpclient.getConnectionManager().shutdown();
	}
	
	protected Map<String, String> makeRequest(String url) throws ClientProtocolException, IOException
	{
		Map<String, String> responseMap = new HashMap<String, String>();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		responseMap.put("responseCode", Integer.toString(response.getStatusLine().getStatusCode()));
		responseMap.put("responseText", EntityUtils.toString(response.getEntity()));
		String contentType = (ContentType.get(response.getEntity()) != null) ? ContentType.get(response.getEntity()).getMimeType().toString() : "";
		responseMap.put("responseContentType", contentType);
		return responseMap;
	}
	
	protected Server createAppServer(Servlet servlet, Filter filter) throws Exception
	{
		System.setProperty("java.naming.factory.url.pkgs", "org.eclipse.jetty.jndi");
		System.setProperty("java.naming.factory.initial", "com.caplin.cutlass.test.TestContextFactory");

		Server appServer = new Server(serverPort);
		ServletContextHandler handler = new ServletContextHandler();
		
		handler.addServlet(new ServletHolder(servlet), "/*");
		handler.addFilter(new FilterHolder(filter), "/*", null);
		appServer.setHandler(handler);
		
		return appServer;
	}
}
