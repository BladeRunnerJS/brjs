package com.caplin.cutlass.app.servlet;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.bladerunnerjs.utility.ServerUtility;

import com.caplin.cutlass.app.RestApiServletTestUtils;
import com.caplin.cutlass.app.service.RestApiService;
import com.caplin.cutlass.app.servlet.RestApiServlet;
import com.caplin.cutlass.util.FileUtility;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("deprecation")
public class RestApiServletTest
{
	
	private static final int HTTP_PORT = ServerUtility.getTestPort();
	private static final String CONTEXT_ROOT = "/some/context";
	private static final String URL_BASE = "http://localhost:"+HTTP_PORT+CONTEXT_ROOT;
	
	private RestApiService service;
	private Server server;
	private HttpClient client;
	
	@Before
	public void setup() throws Exception
	{
		service = mock(RestApiService.class);
		File testSdk = FileUtility.createTemporaryDirectory("RestApiServletTest");
		server = RestApiServletTestUtils.createServer(CONTEXT_ROOT, HTTP_PORT, new RestApiServlet(service), testSdk);
		server.start();
		client = new DefaultHttpClient();
	}
	
	@After
	public void tearDown() throws Exception
	{
		if (server != null)
		{
			server.stop();
		}
		if (client != null)
		{
			client.getConnectionManager().shutdown();
		}
		verifyNoMoreInteractions(service);
	}

	
	/* GET method tests */
	
	@Test
	public void testListApps() throws Exception
	{
		when(service.getApps()).thenReturn("<some list of apps>");
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/apps");
		verify(service,times(1)).getApps();
		assertEquals( "<some list of apps>", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}	
	
	@Test
	public void testGetSingleApp() throws Exception
	{
		when(service.getApp("some-app")).thenReturn("<some app info>");
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/apps/some-app");
		verify(service,times(1)).getApp("some-app");
		assertEquals( "<some app info>", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testGetAppThumbnail() throws Exception
	{
		RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/apps/some-app/thumb");
		verify(service,times(1)).getAppImageInputStream("some-app");
	}
	
	@Test
	public void testGetCurrentReleaseNote() throws Exception
	{
		when(service.getCurrentReleaseNotes()).thenReturn("release note contents...");
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/note/latest");
		verify(service,times(1)).getCurrentReleaseNotes();
		assertEquals( "release note contents...", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testGetVersion() throws Exception
	{
		when(service.getSdkVersion()).thenReturn("version info...");
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/sdk/version");
		verify(service,times(1)).getSdkVersion();
		assertEquals( "version info...", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testExportApp() throws Exception
	{
		RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/export/some-app");
		verify(service,times(1)).exportWar(eq("some-app"), any(File.class));
	}
	
	
	/* POST method tests */
	
	@Test
	public void testImportMotif() throws Exception
	{
		HttpPost httppost = new HttpPost(URL_BASE+"/apps/my-imported-app");
		File uploadZip = new File("src/test/resources/RestApiServiceTest/single-bladeset-single-blade-app.zip");	
		 
		MultipartEntity entity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
		entity.addPart( "file", new FileBody(( uploadZip ), "application/zip" ));
		entity.addPart( "command", new StringBody( "import-motif", "text/plain",  Charset.forName( "UTF-8" )));
		entity.addPart( "namespace", new StringBody( "nsx", "text/plain",  Charset.forName( "UTF-8" )));
		httppost.setEntity( entity );

		HttpResponse response = client.execute(httppost);
		
		verify(service,times(1)).importMotif(eq("my-imported-app"), eq("nsx"), any(File.class));
		assertEquals( "", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testCreateNewApp() throws Exception
	{
		String jsonBody = "{\n" +
				"command:\"create-app\",\n" +
				"namespace:\"nsx\"" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/apps/some-app", jsonBody);
		verify(service,times(1)).createApp("some-app", "nsx");
		assertEquals( "", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testCreateNewBladeset() throws Exception
	{
		String jsonBody = "{\n" +
				"command:\"create-bladeset\"\n" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/apps/some-app/myBladeset", jsonBody);
		verify(service,times(1)).createBladeset("some-app", "myBladeset");
		assertEquals( "", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testImportBlades() throws Exception
	{
		Map<String,Map<String,List<String>>> bladesets = new HashMap<String,Map<String,List<String>>>();
		Map<String,List<String>> someBladeset = new HashMap<String,List<String>>();
		someBladeset.put("newBladesetName", Arrays.asList("someBladeset"));
		someBladeset.put("blades", Arrays.asList("blade1","blade2","blade9"));
		bladesets.put("someBladeset", someBladeset);
		Map<String,List<String>> anotherBladeset = new HashMap<String,List<String>>();
		anotherBladeset.put("newBladesetName", Arrays.asList("anotherBladeset"));
		anotherBladeset.put("blades", Arrays.asList("someBlade","anotherBlade"));
		bladesets.put("anotherBladeset", anotherBladeset);
		
		String jsonBody = "{\n" +
				"command:\"import-blades\",\n" +
				"app:\"anotherApp\"," +
				"bladesets:{someBladeset:{newBladesetName:\"someBladeset\",blades:[\"blade1\",\"blade2\",\"blade9\"]}\n" +
					",anotherBladeset:{newBladesetName:\"anotherBladeset\",blades:[\"someBlade\",\"anotherBlade\"]}}\n" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/apps/some-app", jsonBody);
		verify(service,times(1)).importBladeset("anotherApp", bladesets, "some-app");
		assertEquals( "", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testCreateBlade() throws Exception
	{
		String jsonBody = "{\n" +
				"command:\"create-blade\"\n" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/apps/some-app/myBladeset/myNewBlade", jsonBody);
		verify(service,times(1)).createBlade("some-app", "myBladeset", "myNewBlade");
		assertEquals( "", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void testShowJsDoc() throws Exception
	{
		String jsonBody = "{\n" +
				"command: 'generate-docs'" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/apps/some-app", jsonBody);
		verify(service,times(1)).getJsdocForApp("some-app");
		assertEquals( "", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}

	@Test
	public void testRunBladesetTests() throws Exception
	{
		when(service.runBladesetTests(anyString(), anyString(), anyString())).thenReturn("<some test results>");
		String jsonBody = "{\n" +
				"command:\"test\"," +
				"type:\"ALL\"," +
				"recurse:\"false\"" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/test/some-app/bladeset", jsonBody);
		verify(service,times(1)).runBladesetTests("some-app", "bladeset", "ALL");
		assertEquals( "<some test results>", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}

	@Test
	public void testRunBladeTests() throws Exception
	{
		when(service.runBladeTests(anyString(), anyString(), anyString(), anyString())).thenReturn("<some test results>");
		String jsonBody = "{\n" +
				"command:\"test\"," +
				"type:\"ALL\"" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/test/some-app/bladeset/blade1", jsonBody);
		verify(service,times(1)).runBladeTests("some-app", "bladeset", "blade1", "ALL");
		assertEquals( "<some test results>", RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}

	
	@Test
	public void test500ResponseReturnedForGetError() throws Exception
	{
		String errorJsonBody = "{" +
				"\"cause\":\"java.lang.Exception\"," +
				"\"message\":\"ERROR\"" +
				"}";
		when(service.getApp("some-app")).thenThrow(new Exception("ERROR"));
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/apps/some-app");
		verify(service,times(1)).getApp("some-app");
		assertEquals(500, response.getStatusLine().getStatusCode());
		assertEquals( errorJsonBody, RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void test500ResponseReturnedForPostError() throws Exception
	{
		String errorJsonBody = "{" +
				"\"cause\":\"java.lang.Exception\"," +
				"\"message\":\"ERROR!\"" +
				"}";
		String jsonBody = "{\n" +
				"\"command\":\"create-app\",\n" +
				"\"namespace\":\"nsx\"" +
				"}";
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				throw new Exception("ERROR!");
			}
		}).when(service).createApp(anyString(), anyString());
		
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/apps/some-app", jsonBody);
		verify(service,times(1)).createApp("some-app", "nsx");
		assertEquals(500, response.getStatusLine().getStatusCode());
		assertEquals( errorJsonBody, RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}	
	
	@Test
	public void test404ResponseReturnedForUnknownGetUrl() throws Exception
	{
		String errorJsonBody = "{" +
				"\"cause\":\"Not Found\"," +
				"\"message\":\"Invalid URL ("+URL_BASE+"/unknown-url) - page not found.\"" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "GET", URL_BASE+"/unknown-url");
		assertEquals(404, response.getStatusLine().getStatusCode());
		assertEquals( errorJsonBody, RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
	@Test
	public void test404ResponseReturnedForUnknownPostUrl() throws Exception
	{
		String errorJsonBody = "{" +
				"\"cause\":\"Not Found\"," +
				"\"message\":\"Invalid URL ("+URL_BASE+"/unknown-url) - page not found.\"" +
				"}";
		HttpResponse response = RestApiServletTestUtils.makeRequest(client, "POST", URL_BASE+"/unknown-url");
		assertEquals(404, response.getStatusLine().getStatusCode());
		assertEquals( errorJsonBody, RestApiServletTestUtils.getResponseTextFromResponse(response) );
	}
	
}
