package com.caplin.cutlass.filter.versionfilter;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.utility.ServerUtility;

public class VersionRedirectFilterTest {

	private static final int PORT = ServerUtility.getTestPort();
	private Server appServer;
	private HttpClient httpclient;
		
	@Before
	public void setup() throws Exception {
		httpclient = new DefaultHttpClient();
		appServer = createServer(PORT, "/app1", "src/test/resources/version-redirect/app1");
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
	public void testMaxAgeIsntNegativeOrZero() {
		assertTrue(VersionRedirectFilter.maxAge > 0);
	}
	
	@Test
	public void testUrlIsRewrittenIfItContainsVersionString() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/v_1234/index.html");
		assertEquals( "/app1/index.html", responseMap.get("responseText") );
		assertEquals( "text/html", responseMap.get("responseContentType") );
	}
	
	@Test
	public void appContextCanBeAtAnyLevel() throws Exception {
		int port = PORT+1;
		Server appServer = createServer(port, "/dir1/dir2/app1", "src/test/resources/version-redirect/app1");
		Map<String, String> responseMap = makeRequest("http://localhost:"+port+"/dir1/dir2/app1/v_987/index.html");
		assertEquals( "/app1/index.html", responseMap.get("responseText") );
		assertEquals( "text/html", responseMap.get("responseContentType") );
		appServer.stop();
	}
	
	@Test
	public void testAppCanHaveASimilarFormatToAVersionString() throws Exception {
		int port = PORT+1;
		Server appServer = createServer(port, "/v987", "src/test/resources/version-redirect/v987");
		Map<String, String> responseMap = makeRequest("http://localhost:"+port+"/v987/v_987/index.html");
		assertEquals( "/v987/index.html", responseMap.get("responseText") );
		assertEquals( "text/html", responseMap.get("responseContentType") );
		appServer.stop();
	}
	
	@Test
	public void testVersionStringCanAppearAfterASection() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/login-aspect/v_1234/index.html");
		assertEquals( "/app1/login-aspect/index.html", responseMap.get("responseText") );
		assertEquals( "text/html", responseMap.get("responseContentType") );
	}
	
	@Test
	public void testCachingHeadersAreSetIfUrlContainsAVersionString() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/v_1234/index.html");
		long expiresAgeDifference = getExpiresDifference(responseMap.get("headerExpires"));
		assertEquals( "max-age="+VersionRedirectFilter.maxAge+", public, must-revalidate", responseMap.get("headerCacheControl") );
		assertTrue( Math.abs(expiresAgeDifference) > 0);
		assertTrue( Math.abs(expiresAgeDifference) > (VersionRedirectFilter.maxAge-10));
		assertTrue( Math.abs(expiresAgeDifference) <= (VersionRedirectFilter.maxAge));
		assertEquals( "text/html", responseMap.get("responseContentType") );
		// we cannot check the actual value here because the expires header is based on the time when processing the request
	}
	
	@Test
	public void testCachingHeadersAreNotSetIfUrlDoesNotContainAVersionString() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/index.html");
		assertEquals( "", responseMap.get("headerCacheControl") );
		assertEquals( "", responseMap.get("headerExpires") );
		assertEquals( "text/html", responseMap.get("responseContentType") );
	}
	
	@Test
	public void testCachingHeadersAreNotSetIfRequestIsForAJspPage() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/v_1234/index.jsp");
		assertEquals( "", responseMap.get("headerExpires") );
		assertEquals( "", responseMap.get("headerLastModified") );
		assertTrue( responseMap.get("headerCacheControl").contains("no-cache") );
		assertFalse( responseMap.get("headerCacheControl").contains("max-age") );
	}

	@Test
	public void testCachingHeadersAreNotSetIfRequestIsForAServlet() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/v_1234/servlet/aServlet");
		assertEquals( "", responseMap.get("headerExpires") );
		assertEquals( "", responseMap.get("headerLastModified") );
		assertTrue( responseMap.get("headerCacheControl").contains("no-cache") );
		assertFalse( responseMap.get("headerCacheControl").contains("max-age") );
		assertEquals( "this is the servlets content", responseMap.get("responseText") );
	}
	
	@Test
	public void testVaryHeaderIsSentForIndexHtml() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/index.html");
		assertEquals( "Cookie", responseMap.get("headerVary") );
	}
	
	@Test
	public void testVaryHeaderIsSentForRootUrl() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/");
		assertEquals( "Cookie", responseMap.get("headerVary") );
	}
	
	@Test
	public void testVaryHeaderIsNotSentForRequestThatIsNotIndexOrRootUrl() throws Exception {
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/file.txt");
		assertEquals( "", responseMap.get("headerVary") );
	}
	
	@Test
	public void testETagHeaderIsNeverSet() throws Exception {
		List<String> requests = Arrays.asList( 
				"http://localhost:"+PORT+"/app1/v_1234/",
				"http://localhost:"+PORT+"/app1/v_1234/index.jsp",
				"http://localhost:"+PORT+"/app1/v_1234/index.html",
				"http://localhost:"+PORT+"/app1/v_1234/some-path/file.html"
		);
		
		for (String request : requests)
		{
			Map<String, String> responseMap = makeRequest(request);
			assertEquals( request, "", responseMap.get("headerETag") );			
		}
	}
	
	@Test
	public void testUrlIsUnchangedIfItDoesntContainAVersionString() throws Exception {
		Map<String, String> responseMap;
		
		responseMap = makeRequest("http://localhost:"+PORT+"/app1/index.html");
		assertEquals( "/app1/index.html", responseMap.get("responseText") );
		
		responseMap = makeRequest("http://localhost:"+PORT+"/app1/main-aspect/index.html");
		assertEquals( "/app1/main-aspect/index.html", responseMap.get("responseText") );
	}
	
	@Test
	public void testUrlWithTrailingSlashAndVerisonIsRewritten() throws Exception {
		Map<String, String> responseMap;
		
		responseMap = makeRequest("http://localhost:"+PORT+"/app1/v_1234/");
		assertEquals( "/app1/index.html", responseMap.get("responseText") );
	}
	
	@Test
	public void testUrlWithTrailingSlashAndNoVerisonIsUnchanged() throws Exception {
		Map<String, String> responseMap;
		
		responseMap = makeRequest("http://localhost:"+PORT+"/app1/");
		assertEquals( "/app1/index.html", responseMap.get("responseText") );
	}
	
	@Test
	public void testLastModifiedHeaderIsNotSet() throws Exception {
		Map<String, String> responseMap;
		
		responseMap = makeRequest("http://localhost:"+PORT+"/app1/");
		assertEquals( "200", responseMap.get("responseCode") );
		assertEquals( "", responseMap.get("headerLastModified") );
	}
	
	private Map<String, String> makeRequest(String url) throws ClientProtocolException, IOException {
		Map<String, String> responseMap = new HashMap<String, String>();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		responseMap.put("responseCode", Integer.toString(response.getStatusLine().getStatusCode()) );
		responseMap.put("responseText", EntityUtils.toString(response.getEntity()) );
		String contentType = (ContentType.get(response.getEntity()) != null) ? ContentType.get(response.getEntity()).getMimeType().toString() : "";
		responseMap.put("responseContentType", contentType);
		
		String cacheControl = (response.getFirstHeader("Cache-Control")!=null)?response.getFirstHeader("Cache-Control").getValue():"";
		String expires = (response.getFirstHeader("Expires")!=null)?response.getFirstHeader("Expires").getValue():"";
		String lastModified = (response.getFirstHeader("Last-Modified")!=null)?response.getFirstHeader("Last-Modified").getValue():"";
		String vary = (response.getFirstHeader("Vary")!=null)?response.getFirstHeader("Vary").getValue():"";
		String etag = (response.getFirstHeader("ETag")!=null)?response.getFirstHeader("ETag").getValue():"";
		responseMap.put("headerCacheControl", cacheControl );
		responseMap.put("headerExpires", expires );
		responseMap.put("headerLastModified", lastModified );
		responseMap.put("headerVary", vary );
		responseMap.put("headerETag", etag );
		return responseMap;
	}
	
	private Server createServer(int port, String contextPath, String resourceBase) throws Exception {
		Server appServer = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(contextPath);
		context.setResourceBase(resourceBase);
		context.addServlet(DefaultServlet.class, "/*");
		
		context.addFilter(new FilterHolder(new VersionRedirectFilter()), "/*", EnumSet.of(DispatcherType.FORWARD,DispatcherType.REQUEST));
		appServer.setHandler(context);
		appServer.start();
		
		
		return appServer;
	}
	
	private long getExpiresDifference(String expiresHeader) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(VersionRedirectFilter.headerDateFormat);
		Date expiresDate = formatter.parse(expiresHeader);
		Date now = new Date();
		long expiresDifference = (now.getTime() - expiresDate.getTime())/1000;
		return expiresDifference;
	}
}
