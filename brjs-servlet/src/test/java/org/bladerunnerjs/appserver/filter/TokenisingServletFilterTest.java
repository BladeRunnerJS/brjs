package org.bladerunnerjs.appserver.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.appserver.util.JndiTokenFinder;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TokenisingServletFilterTest extends ServletFilterTest
{
	private Context mockJndiContext;
	private Server appServer;
	private DummyServlet dummyServlet;

	@Before
	public void setup() throws Exception
	{
		mockJndiContext = TestContextFactory.getTestContext();
		dummyServlet = new DummyServlet();
		dummyServlet.resetResponse();
		appServer = createAndStartAppServer(dummyServlet, new TokenisingServletFilter(new JndiTokenFinder(mockJndiContext)));
	}

	@After
	public void teardown() throws Exception
	{
		verifyNoMoreInteractions(mockJndiContext);
		appServer.stop();
	}

	@Test
	public void basicTestForDummyServlet() throws Exception
	{
		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/file.xml");

		assertEquals("200", response.get("responseCode"));
		assertEquals("OK", response.get("responseText"));
		assertEquals("text/plain", response.get("responseContentType"));
	}

	@Test
	public void testTextWithNoTokenIsUnchanged() throws Exception
	{
		String servletText = "I am some text, and I don't contain any tokens.";
		dummyServlet.setResponseText(servletText);

		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/file.xml");
		assertEquals("200", response.get("responseCode"));
		assertEquals(servletText, response.get("responseText"));
		assertEquals("text/plain", response.get("responseContentType"));
	}

	@Test
	public void testServletResponseCanContainHtml() throws Exception
	{
		String servletText = "<div><h1><a href='.'>This is some html<a></h1></div>";
		dummyServlet.setResponseText(servletText);
		dummyServlet.setContentType("text/html");

		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/file.xml");
		assertEquals("200", response.get("responseCode"));
		assertEquals(servletText, response.get("responseText"));
		assertEquals("text/html", response.get("responseContentType"));
	}

	@Test
	public void testJndiIsLookupPerformedForToken() throws Exception
	{
		dummyServlet.setResponseText("@A.TOKEN@");
		when(mockJndiContext.lookup("java:comp/env/A.TOKEN")).thenReturn("token replacement");

		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/file.xml");
		verify(mockJndiContext, times(1)).lookup("java:comp/env/A.TOKEN");
		assertEquals("200", response.get("responseCode"));
		assertEquals("token replacement", response.get("responseText"));
		assertEquals("text/plain", response.get("responseContentType"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test500ResponseCodeIfTokenCannotBeReplaced() throws Exception
	{
		dummyServlet.setResponseText("@A.NONEXISTANT.TOKEN@");
		when(mockJndiContext.lookup("java:comp/env/A.NONEXISTANT.TOKEN")).thenThrow(NamingException.class);

		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/file.xml");
		verify(mockJndiContext, times(1)).lookup("java:comp/env/A.NONEXISTANT.TOKEN");
		assertEquals("500", response.get("responseCode"));
	}
	
	@Test
	public void testTokenisingFilterOnlyProcessesXmlAndJsonFiles() throws Exception
	{
		dummyServlet.setResponseText("this token @A.TOKEN@ should not be processed");

		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/file.js");
		verify(mockJndiContext, never()).lookup("java:comp/env/A.TOKEN");
		assertEquals("200", response.get("responseCode"));
		assertEquals("this token @A.TOKEN@ should not be processed", response.get("responseText"));
	}
	
	@Test
	public void tokenReplacementWorksForIndexPages() throws Exception
	{
		dummyServlet.setResponseText("@A.TOKEN@");
		when(mockJndiContext.lookup("java:comp/env/A.TOKEN")).thenReturn("token replacement");

		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/");
		verify(mockJndiContext, times(1)).lookup("java:comp/env/A.TOKEN");
		assertEquals("200", response.get("responseCode"));
		assertEquals("token replacement", response.get("responseText"));
		assertEquals("text/plain", response.get("responseContentType"));
	}
	
	@Test
	public void tokenReplacementWorksForLocalizedIndexPages() throws Exception
	{
		dummyServlet.setResponseText("@A.TOKEN@");
		when(mockJndiContext.lookup("java:comp/env/A.TOKEN")).thenReturn("token replacement");

		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/en_GB");
		verify(mockJndiContext, times(1)).lookup("java:comp/env/A.TOKEN");
		assertEquals("200", response.get("responseCode"));
		assertEquals("token replacement", response.get("responseText"));
		assertEquals("text/plain", response.get("responseContentType"));
	}

	@Test
	public void tokenReplacementDoesntHappenForThingsThatLooksLikeLocalizedIndexPages() throws Exception
	{
		dummyServlet.setResponseText("this token @A.TOKEN@ should not be processed");

		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/en_gb");
		verify(mockJndiContext, never()).lookup("java:comp/env/A.TOKEN");
		assertEquals("200", response.get("responseCode"));
		assertEquals("this token @A.TOKEN@ should not be processed", response.get("responseText"));
	}
	
	@Test
	public void testFilterDoesNotChokeOnAStreamOnNonTextBits() throws Exception
	{
		FileUtils.copyFileToDirectory( new File("src/test/resources/br-logo.png"), contextDir );
		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/br-logo.png");
		assertEquals("200", response.get("responseCode"));
	}

}
