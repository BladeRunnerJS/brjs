package com.caplin.cutlass.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.bladerunnerjs.model.BRJS;

import com.caplin.cutlass.util.FileUtility;

import org.bladerunnerjs.utility.ServerUtility;

import com.caplin.cutlass.ServletModelAccessor;
import com.caplin.cutlass.app.servlet.RestApiServlet;


public class RestApiServletEndToEndTests
{
	
	private static final int HTTP_PORT = ServerUtility.getTestPort();
	private static final String CONTEXT_ROOT = "/some/context";
	private static final String URL_BASE = "http://localhost:"+HTTP_PORT+CONTEXT_ROOT;
	
	private Server server;
	private HttpClient client;
	
	private BRJS brjs;
	
	@Before
	public void setup() throws Exception
	{
		File sdkRoot = FileUtility.createTemporarySdkInstall(new File("src/test/resources/RestApiServiceTest/no-apps"));
		ServletModelAccessor.destroy();
		brjs = ServletModelAccessor.initializeAndGetModel( sdkRoot );
		
		server = RestApiServletTestUtils.createServer(CONTEXT_ROOT, HTTP_PORT, new RestApiServlet(), sdkRoot);
		server.start();
		client = new DefaultHttpClient();
	}
	
	@After
	public void tearDown() throws Exception
	{
		ServletModelAccessor.destroy();
		
		if (server != null)
		{
			server.stop();
		}
		if (client != null)
		{
			client.getConnectionManager().shutdown();
		}
	}

	@Test
	public void testListEmptyApps() throws Exception
	{
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/apps");
		assertEquals( "[]", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testListMultipleApps() throws Exception
	{
		createApp("anotherNewApp","nsx");
		createApp("newApp","appx");
		
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/apps");
		assertEquals( "[\"anotherNewApp\", \"newApp\"]", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testCreateNewApp() throws Exception
	{
		createApp("newApp","appx");
		
		assertTrue( brjs.app("newApp").dirExists() );
		assertTrue( brjs.app("newApp").aspect("default").assetLocation("src").file("appx").exists() );
	}
	
	@Ignore // this test has been disabled since it is incompatible with the Java7FileModificationService
	@Test
	public void testGettingASingleApp() throws Exception
	{
		HttpResponse response = null;
		
		createApp("newApp","appx");
		response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/apps/newApp");
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals( "{}", RestApiServletTestUtils.getResponseTextFromResponse(response) );
		
		createBladeset("newApp","newbladeset");
		response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/apps/newApp");
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals( "{\"newbladeset\":[]}", RestApiServletTestUtils.getResponseTextFromResponse(response) );
		
		createBlade("newApp","newbladeset","a");
		createBlade("newApp","newbladeset","b");
		createBlade("newApp","newbladeset","c");
		response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/apps/newApp");
		assertEquals(200, response.getStatusLine().getStatusCode());
		/* order of blades doesnt matter here - they will be returned in alphabetical order */
		assertEquals( "{\"newbladeset\":[\"a\", \"b\", \"c\"]}", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testGettingASingleAppWithMultipleBladesetsAndBlades() throws Exception
	{
		HttpResponse response = null;
		
		createApp("myNewTestApp","appx");
		createBladeset("myNewTestApp","newbladeset");
		createBlade("myNewTestApp","newbladeset","a");
		createBlade("myNewTestApp","newbladeset","b");
		createBlade("myNewTestApp","newbladeset","c");
		createBladeset("myNewTestApp","another");
		createBlade("myNewTestApp","another","m");
		createBlade("myNewTestApp","another","n");
		createBlade("myNewTestApp","another","o");
		createBladeset("myNewTestApp","yetanother");
		createBlade("myNewTestApp","yetanother","x");
		createBlade("myNewTestApp","yetanother","y");
		createBlade("myNewTestApp","yetanother","z");
		
		/* order of blades doesnt matter here - they will be returned in alphabetical order */
		String expectedResponse = "{"+
				"\"another\":[\"m\", \"n\", \"o\"], "+
				"\"newbladeset\":[\"a\", \"b\", \"c\"], "+
				"\"yetanother\":[\"x\", \"y\", \"z\"]"+
				"}";
		
		response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/apps/myNewTestApp");
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(expectedResponse, RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testGettingTheImageForAnApp() throws Exception
	{
		createApp("newApp","appx");
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/apps/newApp/thumb");
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals("image/png",response.getFirstHeader("Content-Type").getValue());
	}
	
	@Test
	public void testExportingTheWar() throws Exception
	{
		createApp("newApp","appx");
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/export/newApp");
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals("application/octet-stream",response.getFirstHeader("Content-Type").getValue());
		assertEquals("attachment; filename=\"newApp.war\"",response.getFirstHeader("Content-Disposition").getValue());
		assertTrue( Integer.parseInt(response.getFirstHeader("Content-Length").getValue()) > 0) ;
	}
	
	
	
	/* helper methods */
	
	private HttpResponse createApp(String app, String namespace) throws IOException
	{
		return createApp(app, namespace, true);
	}
	private HttpResponse createApp(String app, String namespace, boolean releaseConnection) throws IOException 
	{
		String jsonBody = "{\n" +
				"command:\"create-app\",\n" +
				"namespace:\""+namespace+"\"" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/apps/"+app, jsonBody);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertTrue( brjs.app(app).dirExists() );
		if (releaseConnection)
		{
			RestApiServletTestUtils.getResponseTextFromResponse(response);
		}
		return response;
	}
	
	private HttpResponse createBladeset(String app, String bladeset) throws IOException
	{
		return createBladeset(app, bladeset, true);
	}
	private HttpResponse createBladeset(String app, String bladeset, boolean releaseConnection) throws IOException 
	{
		String jsonBody = "{\n" +
				"command:\"create-bladeset\"\n" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/apps/"+app+"/"+bladeset, jsonBody);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertTrue( brjs.app(app).bladeset(bladeset).dirExists() );
		if (releaseConnection)
		{
			RestApiServletTestUtils.getResponseTextFromResponse(response);
		}
		return response;
	}
	
	private HttpResponse createBlade(String app, String bladeset, String blade) throws IOException
	{
		return createBlade(app, bladeset, blade, true);
	}
	private HttpResponse createBlade(String app, String bladeset, String blade, boolean releaseConnection) throws IOException 
	{
		String jsonBody = "{\n" +
				"command:\"create-blade\"\n" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/apps/"+app+"/"+bladeset+"/"+blade, jsonBody);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertTrue( brjs.app(app).bladeset(bladeset).blade(blade).dirExists() );
		if (releaseConnection)
		{
			RestApiServletTestUtils.getResponseTextFromResponse(response);
		}
		return response;
	}
	
}
