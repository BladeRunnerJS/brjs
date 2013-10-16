package com.caplin.cutlass.filter.sectionfilter;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.sinbin.CutlassConfig;
import org.bladerunnerjs.model.utility.FileUtility;
import org.bladerunnerjs.model.utility.ServerUtility;

import com.caplin.cutlass.testing.BRJSTestFactory;
import com.caplin.cutlass.util.UrlEchoServlet;

public class SectionRedirectFilterTest
{
	private static final int PORT = ServerUtility.getTestPort();
	
	private static final String APP_LOCATION = "src/test/resources/section-redirect/app1";
	private static final String APP2_LOCATION = "src/test/resources/section-redirect/app2";
	private SectionRedirectHandler handler;
	private Server appServer;
	private HttpClient httpclient;
	
	private File tempDir;
	private File temporaryDirectoryForWebApp;

	@Before
	public void setup() throws Exception
	{
		handler = new SectionRedirectHandler(BRJSTestFactory.createBRJS(new File(APP_LOCATION)), new File(APP_LOCATION));
		tempDir = FileUtility.createTemporaryDirectory(this.getClass().getName());
		temporaryDirectoryForWebApp = new File(tempDir,"app1_");
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
		
		FileUtility.deleteDirAndContents(tempDir);
	}

	@Test
	public void noSectionIsRedirectedTodefaultSection()
	{
		// /app1 -> /app1/default-aspect/
		assertEquals("/default-aspect/", handler.getRedirectUrl("/"));
	}
	
	@Test
	public void explicitdefaultSectionIsRedirectedTodefaultSection()
	{
		// /app1/default -> /app1/default-aspect/
		assertEquals("/default-aspect/", handler.getRedirectUrl("/default"));
	}
		
	@Test
	public void checkThatIfWeHaveAlreadyRedirectedThisUrlThatWeDontChangeIt()
	{
		// /app1/default -> /app1/default-aspect/
		assertEquals("/default-aspect/", handler.getRedirectUrl("/default-aspect"));
	}
	
	@Test
	public void bundleWithNoSectionIsRedirectedTodefaultSectionIncludingBundle()
	{
		// /app1/xml.bundle -> /app1/default-aspect/xml.bundle
		assertEquals("/default-aspect/xml.bundle", handler.getRedirectUrl("/xml.bundle"));
	}
	
	@Test
	public void bundleUnderDirectoryWithNoSectionIsRedirectedTodefaultSectionIncludingBundle()
	{
		// /app1/css/noir_css.bundle -> /app1/default-aspect/css/noir_css.bundle
		assertEquals("/default-aspect/css/noir_css.bundle", handler.getRedirectUrl("/css/noir_css.bundle"));
	}
	
	@Test
	public void urlWithSectionSpecifiedIsRedirectedToSection()
	{
		// /app1/mobile -> /app1/mobile-aspect/
		assertEquals("/mobile-aspect/", handler.getRedirectUrl("/mobile"));
	}
	
	@Test
	public void urlWithSectionContainingMultipleDashesIsRedirectedToCorrectSection()
	{
		// /app1/mobile-new -> /app1/mobile-new-aspect/
		assertEquals("/mobile-new-aspect/", handler.getRedirectUrl("/mobile-new"));
	}
	
	@Test
	public void urlWithSectionContainingDotsIsRedirectedToCorrectSection()
	{
		// /app1/mobile.hd -> /app1/mobile.hd-aspect/
		assertEquals("/mobile.hd-aspect/", handler.getRedirectUrl("/mobile.hd"));
	}
	
	@Test
	public void urlWithSectionContainingNumbersIsRedirectedToCorrectSection()
	{
		// /app1/mobile2 -> /app1/mobile2-aspect/
		assertEquals("/mobile2-aspect/", handler.getRedirectUrl("/mobile2"));
	}

	@Test
	public void bundleWithNamedSectionIsRedirectedToSectionIncludingBundle()
	{
		// /app1/mobile/xml.bundle -> /app1/mobile-aspect/xml.bundle
		assertEquals("/mobile-aspect/xml.bundle", handler.getRedirectUrl("/mobile/xml.bundle"));
	}
	
	@Test
	public void servletRequestsAreUnchanged()
	{
		assertEquals("/servlet/StandardKeymaster", handler.getRedirectUrl("/servlet/StandardKeymaster"));
		assertEquals("/servlet/webcentric/abc", handler.getRedirectUrl("/servlet/webcentric/abc"));
	}
	
	@Test
	public void bladesetRequestsAreUnchangedIfTheBladesetDirectoryExists()
	{
		assertEquals("/bladeset/blades/blade1/workbench", handler.getRedirectUrl("/bladeset/blades/blade1/workbench"));
		assertEquals("/extra-bladeset/blades/blade1/workbench", handler.getRedirectUrl("/extra-bladeset/blades/blade1/workbench"));
	}
	
	@Test
	public void bladesetRequestssAreForwardedIfTheBladesetDirectoryDoesNotExists()
	{
		assertEquals("/default-aspect/nonexistent-bladeset/blades/blade1/workbench",
			handler.getRedirectUrl("/nonexistent-bladeset/blades/blade1/workbench"));
	}
	
	@Test
	public void checkRedirectToServletUrlFromRequestAtSectionRoot()
	{
		// /app1/default-aspect/servlet/XHRKeymaster -> /app1/servlet/XHRKeymaster
		assertEquals("/servlet/XHRKeymaster", handler.getRedirectUrl("/default-aspect/servlet/XHRKeymaster"));
	}
	
	@Test
	public void checkRedirectToServletUrlFromRequestAtSectionSubdirectory()
	{
		// /app1/default-aspect/subdirectory/servlet/XHRKeymaster -> /app1/servlet/XHRKeymaster
		assertEquals("/servlet/XHRKeymaster", handler.getRedirectUrl("/default-aspect/subdirectory/servlet/XHRKeymaster"));
	}
	
	@Test
	public void checkThirdPartyLibraryResourceRequestsUnchanged()
	{
		// /app1//thirdparty-libraries/lib1/lib1resource.txt -> app1/thirdparty-libraries/lib1/lib1resource.txt
		assertEquals("/thirdparty-libraries/lib1/lib1resource.txt", handler.getRedirectUrl("/thirdparty-libraries/lib1/lib1resource.txt"));
	}
	
	@Test
	public void runningViaServletCausesCorrectRedirect() throws Exception
	{		
		httpclient = new DefaultHttpClient();
		appServer = createServer(PORT, "/app1", APP_LOCATION);
		
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/index.html");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/app1/default-aspect/index.html", responseMap.get("responseText"));
		
		responseMap = makeRequest("http://localhost:"+PORT+"/app1/");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/app1/default-aspect/index.html", responseMap.get("responseText"));
	}
	
	@Test
	public void runningViaServletInDevModeCausesCorrectSectionRedirectsAfterAddingABladeSet() throws Exception
	{		
		httpclient = new DefaultHttpClient();
		
		String app1ContextPath = temporaryDirectoryForWebApp.getName();
		File app1Directory = new File(APP_LOCATION);
		
		FileUtility.copyDirectoryContents(app1Directory, temporaryDirectoryForWebApp);
		appServer = createServer(PORT, "/" + app1ContextPath, temporaryDirectoryForWebApp.getAbsolutePath(), true);

		String newBladeSetDirName = "new-bladeset";
		
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/" + app1ContextPath 
				+ "/" + newBladeSetDirName + "/index.html");
		
		assertEquals("404", responseMap.get("responseCode"));		

		File newBladeSetSourceLocation = new File(APP_LOCATION + "/../new-bladeset-for-app1");
		File newBladeSetDir = new File(temporaryDirectoryForWebApp, newBladeSetDirName);
		newBladeSetDir.mkdir();
		FileUtility.copyDirectoryContents(newBladeSetSourceLocation, newBladeSetDir);
		
		responseMap = makeRequest("http://localhost:"+PORT+"/" + app1ContextPath 
				+ "/" + newBladeSetDirName + "/index.html");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/app1/new-bladeset/index.html", responseMap.get("responseText"));
	}
	
	@Test
	public void runningViaServletWithRootContextCausesCorrectRedirect() throws Exception
	{		
		httpclient = new DefaultHttpClient();
		appServer = createServer(PORT, "/", APP_LOCATION);
		
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/index.html");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/app1/default-aspect/index.html", responseMap.get("responseText"));
	}
	
	@Test
	public void sectionRedirectFilterSupportsHtmExtension() throws Exception
	{	
		handler = new SectionRedirectHandler(BRJSTestFactory.createBRJS(new File(APP2_LOCATION)), new File(APP2_LOCATION));
		httpclient = new DefaultHttpClient();
		appServer = createServer(PORT, "/app2", APP2_LOCATION);
		
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app2/index.htm");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/app2/default-aspect/index.htm", responseMap.get("responseText"));
	}
	
	@Test
	public void sectionRedirectFilterSupportsJspExtension() throws Exception
	{	
		handler = new SectionRedirectHandler(BRJSTestFactory.createBRJS(new File(APP2_LOCATION)), new File(APP2_LOCATION));
		httpclient = new DefaultHttpClient();
		appServer = createServer(PORT, "/app2", APP2_LOCATION);
		
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app2/mobile-aspect/index.jsp");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/app2/mobile-aspect/index.jsp", responseMap.get("responseText"));
	}
	
	@Test
	public void runningViaServletWithUrlWithoutTrailingSlashCauses302RedirectToAddSlash() throws Exception
	{		
		httpclient = new DefaultHttpClient();
		httpclient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		appServer = createServer(PORT, "/app1", APP_LOCATION);
		
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/default");
		assertEquals("302", responseMap.get("responseCode"));
		assertEquals("http://localhost:"+PORT+"/app1/default/", responseMap.get("responseRedirectLocation"));
	}
	
	@Test
	public void doingRedirectForNonTrailingSlashDoesntLooseQueryString() throws Exception
	{	
		httpclient = new DefaultHttpClient();
		httpclient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
		
		appServer = new Server(PORT);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setResourceBase(APP_LOCATION);
		context.setContextPath("/app1");
		context.addServlet(UrlEchoServlet.class, "/*");
		context.addFilter(new FilterHolder(new SectionRedirectFilter()), "/*", null);
		appServer.setHandler(context);
		appServer.start();
		
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/default-aspect/test?testParam=testValue");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/app1/default-aspect/test/?testParam=testValue", responseMap.get("responseText"));
	}
	
	@Test
	public void runningViaServletWithServletRequestDoesntDoA302Redirect() throws Exception
	{		
		httpclient = new DefaultHttpClient();
		httpclient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		appServer = createServer(PORT, "/app1", APP_LOCATION);
		
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/app1/servlet/XHRKeymaster");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("/servlet/XHRKeymaster", responseMap.get("responseText"));
	}
	
	
	private Map<String, String> makeRequest(String url) throws ClientProtocolException, IOException
	{
		Map<String, String> responseMap = new HashMap<String, String>();
		HttpGet httpget = new HttpGet(url);

		HttpResponse response = httpclient.execute(httpget);
		responseMap.put("responseCode", Integer.toString(response.getStatusLine().getStatusCode()));
		Header[] locationHeaders = response.getHeaders("Location");
		if (locationHeaders != null && locationHeaders.length > 0)
		{
			responseMap.put("responseRedirectLocation", locationHeaders[locationHeaders.length - 1].getValue());
		}
		responseMap.put("responseText", EntityUtils.toString(response.getEntity()));
		String contentType = (ContentType.get(response.getEntity()) != null) ? ContentType.get(response.getEntity()).getMimeType().toString() : "";
		responseMap.put("responseContentType", contentType);
		
		return responseMap;
	}

	private Server createServer(int port, String contextPath, String resourceBase) throws Exception
	{
		return createServer(port, contextPath, resourceBase, false);
	}
	
	private Server createServer(int port, String contextPath, String resourceBase, boolean devMode) throws Exception
	{
		Server appServer = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		if(devMode) {
			context.setAttribute(CutlassConfig.DEV_MODE_FLAG, true);
		}
		context.setContextPath(contextPath);
		context.setResourceBase(resourceBase);
		context.addServlet(DefaultServlet.class, "/*");
		context.addFilter(new FilterHolder(new SectionRedirectFilter()), "/*", null);
		appServer.setHandler(context);
		appServer.start();
		
		return appServer;
	}

}
