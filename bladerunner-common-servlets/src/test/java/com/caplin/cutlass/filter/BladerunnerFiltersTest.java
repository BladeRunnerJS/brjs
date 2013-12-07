package com.caplin.cutlass.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.caplin.cutlass.util.FileUtility;

import org.bladerunnerjs.utility.ServerUtility;

import com.caplin.cutlass.ServletModelAccessor;
import com.caplin.cutlass.filter.tokenfilter.StreamTokeniser;
import com.caplin.cutlass.test.TestContextFactory;

public class BladerunnerFiltersTest
{
	private static final String TEST_BASE = "src/test/resources/cutlass-filters";
	private static int PORT = ServerUtility.getTestPort();
	private static Server appServer;
	private static HttpClient httpclient;
	private static Context mockJndiContext;
	private static File tempSdkInstall;

	@BeforeClass
	public static void suiteSetup() throws Exception
	{
		ServletModelAccessor.reset();
		httpclient = new DefaultHttpClient();
		httpclient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		mockJndiContext = TestContextFactory.getTestContext();
		tempSdkInstall = FileUtility.createTemporarySdkInstall(new File(TEST_BASE)).getParentFile();
		appServer = createServer(PORT, "/apps/app1", new File(tempSdkInstall, "apps/app1").getPath(), mockJndiContext);
	}

	@AfterClass
	public static void suiteTearDown() throws Exception
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

	@Before
	public void setup()
	{

	}

	@After
	public void tearDown()
	{
		verifyZeroInteractions(mockJndiContext);
		reset(mockJndiContext);
	}

	@Test
	public void testUrlWithVersionAndNoSectionIsRewritten() throws Exception
	{
		Map<String, String> responseMap;

		responseMap = makeRequest("http://localhost:"+PORT+"/apps/app1/v_1234/index.html");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/app1/default-aspect/index.html", responseMap.get("responseText"));
	}

	@Test
	public void testUrlCanBeRewrittenAndIsNotHandledByTokenisingFilterIfNotARecognisedFileType() throws Exception
	{
		Map<String, String> responseMap;

		responseMap = makeRequest("http://localhost:"+PORT+"/apps/app1/v_1234/file.txt");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/app1/default-aspect/file.txt  - @A.TOKEN@", responseMap.get("responseText"));
		verifyZeroInteractions(mockJndiContext);
	}

	@Test
	public void testUrlCanBeRewrittenAndTokensAreReplacedIfARecognisedFileType() throws Exception
	{
		Map<String, String> responseMap;
		responseMap = makeRequest("http://localhost:"+PORT+"/apps/app1/v_1234/file.xml");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/app1/default-aspect/file.xml  - token replacement", responseMap.get("responseText"));
	}
	
	@Test
	public void testFiltersDontRemoveContentTypeHeader() throws Exception
	{
		Map<String, String> responseMap;
		responseMap = makeRequest("http://localhost:"+PORT+"/apps/app1/file.js");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("text/javascript", responseMap.get("responseContentType"));
	}

	@Test
	public void testFiltersAddContentTypeForBundle() throws Exception
	{
		Map<String, String> responseMap;
		responseMap = makeRequest("http://localhost:"+PORT+"/apps/app1/js/js.bundle");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("text/javascript", responseMap.get("responseContentType"));
	}
	
	@Test
	public void testFiltersDontRemoveContentTypeHeaderForUrlWithVersion() throws Exception
	{
		Map<String, String> responseMap;
		responseMap = makeRequest("http://localhost:"+PORT+"/apps/app1/v_1234/file.js");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("text/javascript", responseMap.get("responseContentType"));
	}

	@Test
	public void testFiltersDontRemoveContentTypeHeaderForFilteredContent() throws Exception
	{
		Map<String, String> responseMap;
		responseMap = makeRequest("http://localhost:"+PORT+"/apps/app1/v_1234/file.xml");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("text/xml", responseMap.get("responseContentType"));
	}
	
	@Test
	public void testVersionUrlCanBeATimestamp() throws Exception
	{
		Map<String, String> responseMap;
		responseMap = makeRequest("http://localhost:"+PORT+"/apps/app1/v_"+StreamTokeniser.getAppVersionTimestamp()+"/file.xml");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/app1/default-aspect/file.xml  - token replacement", responseMap.get("responseText"));
		assertEquals("text/xml", responseMap.get("responseContentType"));
	}	
	
	@Test
	public void testRequestForServletResourceIsNotRewritten() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/apps/app1/servlet/XHRKeymaster");
		assertEquals( "200", responseMap.get("responseCode") );
		assertEquals( "/app1/servlet/XHRKeymaster", responseMap.get("responseText") );
	}
	
	@Test
	public void testRequestsDoNotLooseTheirQueryString() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/apps/app1/test/v_1234/echo.bundle?testParam=testValue");
		System.out.println(responseMap.get("responseText"));
		assertEquals( "200", responseMap.get("responseCode") );
		assertEquals( "/apps/app1/test-aspect/echo.bundle?testParam=testValue", responseMap.get("responseText") );
	}
	
	@Test
	public void testRequestsDontHaveInvalidQueryString() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/apps/app1/test/v_1234/echo.bundle");
		assertEquals( "200", responseMap.get("responseCode") );
		assertEquals( "/apps/app1/test-aspect/echo.bundle", responseMap.get("responseText") );
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
	
	private static Server createServer(int port, String contextPath, String resourceBase, Context jndiContext) throws Exception
	{
		System.setProperty("java.naming.factory.url.pkgs", "org.eclipse.jetty.jndi");
		System.setProperty("java.naming.factory.initial", "org.eclipse.jetty.jndi.InitialContextFactory");
		
		XmlConfiguration configuration = new XmlConfiguration(new FileInputStream(new File(tempSdkInstall, "apps/app1/WEB-INF/jetty.xml")));
		Server appServer = (Server) configuration.configure();
		
		Connector c = new SelectChannelConnector();
		c.setPort(port);
		appServer.addConnector(c);
		
		WebAppContext context = new WebAppContext();
		context.setContextPath(contextPath);
		context.setConfigurationClasses(new String[] { 
				"org.eclipse.jetty.webapp.WebInfConfiguration", 
				"org.eclipse.jetty.webapp.WebXmlConfiguration",
				"org.eclipse.jetty.webapp.MetaInfConfiguration",
				"org.eclipse.jetty.webapp.FragmentConfiguration",
				"org.eclipse.jetty.plus.webapp.EnvConfiguration",
				"org.eclipse.jetty.plus.webapp.PlusConfiguration",
				"org.eclipse.jetty.webapp.JettyWebXmlConfiguration"});
		
		context.setResourceBase(new File(tempSdkInstall, "apps/app1").getPath());
		context.setDescriptor("src/test/resources/cutlass-filters/apps/app1/WEB-INF/web.xml");
		appServer.setHandler(context);
		appServer.start();
		return appServer;
	}
}
