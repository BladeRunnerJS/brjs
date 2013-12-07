package com.caplin.cutlass.filter.tokenfilter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

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
import org.junit.Ignore;
import org.junit.Test;
import org.bladerunnerjs.utility.ServerUtility;

import com.caplin.cutlass.filter.DummyServlet;
import com.caplin.cutlass.test.TestContextFactory;

// TODO: ensure this functionality is tested once it becomes a file transform
public class TokenisingServletFilterTest
{
	private static final int PORT = ServerUtility.getTestPort();
	private Context mockJndiContext;

	private Server appServer;
	private DummyServlet dummyServlet;
	private TokenisingServletFilter filter;
	private HttpClient httpclient;

	@Before
	public void setup() throws Exception
	{
		httpclient = new DefaultHttpClient();

		dummyServlet = new DummyServlet();
		dummyServlet.resetResponse();

		mockJndiContext = TestContextFactory.getTestContext();
		filter = new TokenisingServletFilter(new JndiTokenFinder(mockJndiContext));
		
		setupAppServer();
		appServer.start();
	}

	@After
	public void teardown() throws Exception
	{
		verifyNoMoreInteractions(mockJndiContext);
		httpclient.getConnectionManager().shutdown();
		appServer.stop();
	}

	@Ignore
	@Test
	public void basicTestForDummyServlet() throws Exception
	{
		Map<String, String> response = makeRequest("http://localhost:"+PORT+"/file.xml");

		assertEquals("200", response.get("responseCode"));
		assertEquals("OK", response.get("responseText"));
		assertEquals("text/plain", response.get("responseContentType"));
	}

	@Ignore
	@Test
	public void testTextWithNoTokenIsUnchanged() throws Exception
	{
		String servletText = "I am some text, and I don't contain any tokens.";
		dummyServlet.setResponseText(servletText);

		Map<String, String> response = makeRequest("http://localhost:"+PORT+"/file.xml");
		assertEquals("200", response.get("responseCode"));
		assertEquals(servletText, response.get("responseText"));
		assertEquals("text/plain", response.get("responseContentType"));
	}

	@Ignore
	@Test
	public void testServletResponseCanContainHtml() throws Exception
	{
		String servletText = "<div><h1><a href='.'>This is some html<a></h1></div>";
		dummyServlet.setResponseText(servletText);
		dummyServlet.setContentType("text/html");

		Map<String, String> response = makeRequest("http://localhost:"+PORT+"/file.xml");
		assertEquals("200", response.get("responseCode"));
		assertEquals(servletText, response.get("responseText"));
		assertEquals("text/html", response.get("responseContentType"));
	}

	@Ignore
	@Test
	public void testJndiIsLookupPerformedForToken() throws Exception
	{
		dummyServlet.setResponseText("@A.TOKEN@");
		when(mockJndiContext.lookup("java:comp/env/A.TOKEN")).thenReturn("token replacement");

		Map<String, String> response = makeRequest("http://localhost:"+PORT+"/file.xml");
		verify(mockJndiContext, times(1)).lookup("java:comp/env/A.TOKEN");
		assertEquals("200", response.get("responseCode"));
		assertEquals("token replacement", response.get("responseText"));
		assertEquals("text/plain", response.get("responseContentType"));
	}

	@Ignore
	@Test
	public void test500ResponseCodeIfTokenCannotBeReplaced() throws Exception
	{
		dummyServlet.setResponseText("@A.NONEXISTANT.TOKEN@");
		when(mockJndiContext.lookup("java:comp/env/A.NONEXISTANT.TOKEN")).thenReturn(null);

		Map<String, String> response = makeRequest("http://localhost:"+PORT+"/file.xml");
		verify(mockJndiContext, times(1)).lookup("java:comp/env/A.NONEXISTANT.TOKEN");
		assertEquals("500", response.get("responseCode"));
	}

	@Ignore
	@Test
	public void testTokenisingFilterOnlyProcessesXmlAndJsonFiles() throws Exception
	{
		dummyServlet.setResponseText("this token @A.TOKEN@ should not be processed");

		Map<String, String> response = makeRequest("http://localhost:"+PORT+"/file.js");
		verify(mockJndiContext, never()).lookup("java:comp/env/A.TOKEN");
		assertEquals("200", response.get("responseCode"));
		assertEquals("this token @A.TOKEN@ should not be processed", response.get("responseText"));
	}

	@Ignore
	@Test
	public void testFilterDoesNotChokeOnAStreamOnNonTextBits() throws Exception
	{
		Map<String, String> response = makeRequest("http://localhost:"+PORT+"/jollyroger.jpg");
		assertEquals("200", response.get("responseCode"));
	}

	private Map<String, String> makeRequest(String url) throws ClientProtocolException, IOException
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

	private void setupAppServer() throws Exception
	{
		System.setProperty("java.naming.factory.url.pkgs", "org.eclipse.jetty.jndi");
		System.setProperty("java.naming.factory.initial", "com.caplin.cutlass.test.TestContextFactory");

		appServer = new Server(PORT);

		ServletContextHandler handler = new ServletContextHandler();
		
		handler.addServlet(new ServletHolder(dummyServlet), "/*");
		handler.addFilter(new FilterHolder(filter), "/*", null);
		appServer.setHandler(handler);
	}

}
