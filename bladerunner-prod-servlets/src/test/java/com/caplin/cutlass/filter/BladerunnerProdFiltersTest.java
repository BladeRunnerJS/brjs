package com.caplin.cutlass.filter;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.utility.ServerUtility;
import com.caplin.cutlass.ServletModelAccessor;

public class BladerunnerProdFiltersTest {

	private static int PORT = ServerUtility.getTestPort();
	private static Server appServer;
	private static HttpClient httpclient;
	private static final String APP_DIR = "src/test/resources/cutlass-filters/app1";
	
	@Before
	public void setup() throws Exception {
		ServletModelAccessor.reset();
		httpclient = new DefaultHttpClient();
		appServer = createServer(PORT, "/app1", APP_DIR);
	}
	
	@After
	public void tearDown() throws Exception {
		if (appServer != null && appServer.isStarted()) {
			appServer.stop();
		}
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
	}
	
	@Test
	public void testGZippedBundledResourceHasGzipContentEncoding() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/main-aspect/html.bundle");
		assertEquals( "200", responseMap.get("responseCode") );
		assertEquals( "gzip", responseMap.get("responseContentEncoding") );
	}
	
	@Test
	public void testNonGZippedBundledImageResourceDoesNotHaveContentEncoding() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/main-aspect/someImage.png_image.bundle");
		assertEquals( "200", responseMap.get("responseCode") );
		assertEquals( null, responseMap.get("responseContentEncoding") );
	}
	
	@Test
	public void testNonBundledResourceDoesNotHaveContentEncoding() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/main-aspect/index.html");
		assertEquals( "200", responseMap.get("responseCode") );
		assertEquals( null, responseMap.get("responseContentEncoding") );
	}
	
	@Test
	public void testNonValidRequestForBundledResourceHas404ResponseCodeAndDoesNotHaveContentEncoding() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/main-aspect/bla_bla_bla_css.bundle");
		assertEquals( "404", responseMap.get("responseCode") );
		assertEquals( null, responseMap.get("responseContentEncoding") );
	}
	
	@Test
	public void testValidRequestForNonExistingBundledResourceHas200ResponseCodeAndDoesNotHaveContentEncoding() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/main-aspect/css/common_ie6_css.bundle");
		assertEquals( "200", responseMap.get("responseCode"));
		assertEquals( null, responseMap.get("responseContentEncoding"));
	}


	private Map<String, String> makeRequest(String url) throws ClientProtocolException, IOException {
		Map<String, String> responseMap = new HashMap<String, String>();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		responseMap.put("responseCode", Integer.toString(response.getStatusLine().getStatusCode()) );
		responseMap.put("responseContentEncoding", response.getEntity().getContentEncoding() != null ? response.getEntity().getContentEncoding().getValue() : null);
		return responseMap;
	}
	
	private static Server createServer(int port, String contextPath, String resourceBase) throws Exception {
		XmlConfiguration configuration = new XmlConfiguration(new FileInputStream(new File("src/test/resources/cutlass-prod-filters/app1/WEB-INF/jetty.xml")));
		Server appServer = (Server)configuration.configure();
		
		Connector c = new SelectChannelConnector();
		c.setPort(port);
		appServer.addConnector(c);
		
		WebAppContext context = new WebAppContext();
		context.setContextPath(contextPath);
		context.setResourceBase("src/test/resources/cutlass-prod-filters/app1");
		context.setDescriptor("src/test/resources/cutlass-prod-filters/app1/WEB-INF/web.xml");
		appServer.setHandler(context);
		appServer.start();
		return appServer;
	}	
}
