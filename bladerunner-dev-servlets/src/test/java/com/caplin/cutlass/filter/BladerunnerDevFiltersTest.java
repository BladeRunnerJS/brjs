package com.caplin.cutlass.filter;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

import org.bladerunnerjs.model.utility.ServerUtility;
import com.caplin.cutlass.ServletModelAccessor;

public class BladerunnerDevFiltersTest {

	private static int PORT = ServerUtility.getTestPort();
	private static Server appServer;
	private static HttpClient httpclient;
		
	@BeforeClass
	public static void suiteSetup() throws Exception {
		ServletModelAccessor.reset();
		httpclient = new DefaultHttpClient();
		httpclient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		appServer = createServer(PORT, "/app1", "src/test/resources/cutlass-filters/app1");
	}
	
	@AfterClass
	public static void suiteTearDown() throws Exception {		
		if (appServer != null && appServer.isStarted()) {
			appServer.stop();
		}
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
	}
	
	@Before
	public void setup() {
		
	}
	
	@Test
	public void testUrlWithVersionAndNoSectionIsRewritten() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/v_1234/file.xml");
		assertEquals( "404", responseMap.get("responseCode") );
	}
	
	@Test
	public void testRequestForServletResourceIsNotRewritten() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/servlet/XHRKeymaster");
		assertEquals( "200", responseMap.get("responseCode") );
		assertEquals( "/app1/servlet/XHRKeymaster", responseMap.get("responseText") );
	}
	
	@Test
	public void testRequestsDoNotLooseTheirQueryString() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/echo.bundle?testParam=testValue");
		assertEquals( "200", responseMap.get("responseCode") );
		assertEquals( "/app1/echo.bundle?testParam=testValue", responseMap.get("responseText") );
	}

	@Test
	public void testRequestsDontHaveInvalidQueryString() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/v_1234/echo.bundle");
		System.out.println(responseMap.get("responseText"));
		assertEquals( "200", responseMap.get("responseCode") );
		assertEquals( "/app1/v_1234/echo.bundle", responseMap.get("responseText") );
	}
	
	private Map<String, String> makeRequest(String url) throws ClientProtocolException, IOException {
		Map<String, String> responseMap = new HashMap<String, String>();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		responseMap.put("responseCode", Integer.toString(response.getStatusLine().getStatusCode()) );
		responseMap.put("responseText", EntityUtils.toString(response.getEntity()) );
		String contentType = (ContentType.get(response.getEntity()) != null) ? ContentType.get(response.getEntity()).getMimeType().toString() : "";
		responseMap.put("responseContentType", contentType);
		return responseMap;
	}
	
	private static Server createServer(int port, String contextPath, String resourceBase) throws Exception {
		XmlConfiguration configuration = new XmlConfiguration(new FileInputStream(new File("src/test/resources/cutlass-dev-filters/app1/WEB-INF/jetty.xml")));
System.out.println(configuration.configure());
		Server appServer = (Server)configuration.configure();
		
		Connector c = new SelectChannelConnector();
		c.setPort(port);
		appServer.addConnector(c);
		
		WebAppContext context = new WebAppContext();
		context.setContextPath(contextPath);
		context.setResourceBase("src/test/resources/cutlass-dev-filters/app1");
		context.setDescriptor("src/test/resources/cutlass-dev-filters/app1/WEB-INF/web.xml");
		appServer.setHandler(context);
		appServer.start();
		return appServer;
	}	
}
