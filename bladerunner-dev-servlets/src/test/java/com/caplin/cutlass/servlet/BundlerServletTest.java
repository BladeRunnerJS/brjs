package com.caplin.cutlass.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.core.plugin.bundler.LegacyFileBundlerPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.sinbin.AppMetaData;
import org.bladerunnerjs.model.sinbin.CutlassConfig;
import org.bladerunnerjs.model.utility.FileUtility;
import org.bladerunnerjs.model.utility.ServerUtility;
import com.caplin.cutlass.ServletModelAccessor;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import com.caplin.cutlass.bundler.io.BundleWriterFactory;

public class BundlerServletTest
{
	private static final int PORT = ServerUtility.getTestPort();
	private static final String APPS = CutlassConfig.APPLICATIONS_DIR;
	
	private BundlerServlet bundlerServlet;
	private LegacyFileBundlerPlugin mockJsBundler;
	private LegacyFileBundlerPlugin mockCssBundler;
	private LegacyFileBundlerPlugin mockXmlBundler;
	private LegacyFileBundlerPlugin mockImageBundler;
	private Server appServer;
	private HttpClient httpclient;
	private File tempSdkDir;
	
	@Before
	public void setup() throws Exception
	{
		ServletModelAccessor.reset();
		
		httpclient = new DefaultHttpClient();
		
		mockJsBundler = createMockBundler("js.bundle");
		mockCssBundler = createMockBundler("css.bundle");
		mockXmlBundler = createMockBundler("xml.bundle");
		mockImageBundler = createMockBundler("image.bundle");
		List<LegacyFileBundlerPlugin> bundlers = Arrays.asList(mockJsBundler, mockCssBundler, mockXmlBundler, mockImageBundler);
		bundlerServlet = new BundlerServlet(bundlers);
		
		tempSdkDir = FileUtility.createTemporarySdkInstall(new File("src/test/resources/BundlerServlet")).getParentFile();
		appServer = createServer(PORT, "/", tempSdkDir.getPath());
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
	}
	
	private LegacyFileBundlerPlugin createMockBundler(String bundleExtension)
	{
		LegacyFileBundlerPlugin mockBundler = mock(LegacyFileBundlerPlugin.class);
		when(mockBundler.getBundlerExtension()).thenReturn(bundleExtension);
		when(mockBundler.getValidRequestForms()).thenReturn(new ArrayList<String>());
		
		return mockBundler;
	}

	@Test @SuppressWarnings("unchecked")
	public void testServletPassesCallOntoCorrectBundler_BasicFilename_Css() throws Exception
	{
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/css.bundle");
		assertEquals("200", responseMap.get("responseCode"));

		verify(mockCssBundler).getBundleFiles(any(File.class), eq((File) null), eq("css.bundle"));
		verify(mockCssBundler).writeBundle(any(List.class), any(OutputStream.class));
	}

	@Test @SuppressWarnings("unchecked")
	public void testServletPassesCallOntoCorrectBundler_BasicFilename_Js() throws Exception
	{
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/js.bundle");
		assertEquals("200", responseMap.get("responseCode"));

		verify(mockJsBundler).getBundleFiles(any(File.class), eq((File) null), eq("js.bundle"));
		verify(mockJsBundler).writeBundle(any(List.class), any(OutputStream.class));
	}

	@Test @SuppressWarnings("unchecked")
	public void testServletPassesCallOntoCorrectBundler_FilenameWithPrefix_Css() throws Exception
	{
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/abc_css.bundle");
		assertEquals("200", responseMap.get("responseCode"));

		verify(mockCssBundler).getBundleFiles(any(File.class), eq((File) null), eq("abc_css.bundle"));
		verify(mockCssBundler).writeBundle(any(List.class), any(OutputStream.class));
	}

	@Test @SuppressWarnings("unchecked")
	public void testServletPassesCallOntoCorrectBundler_FilenameWithPrefixContainingBundleType_Css() throws Exception
	{
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/my_css_css.bundle");
		assertEquals("200", responseMap.get("responseCode"));

		verify(mockCssBundler).getBundleFiles(any(File.class), eq((File) null), eq("my_css_css.bundle"));
		verify(mockCssBundler).writeBundle(any(List.class), any(OutputStream.class));
	}
	
	@Test @SuppressWarnings("unchecked")
	public void testServletPassesCallOntoCorrectBundler_FilenameWithPrefixContainingBundleType_Js() throws Exception
	{
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/my_js_js.bundle");
		assertEquals("200", responseMap.get("responseCode"));

		verify(mockJsBundler).getBundleFiles(any(File.class), eq((File) null), eq("my_js_js.bundle"));
		verify(mockJsBundler).writeBundle(any(List.class), any(OutputStream.class));
	}

	@Test @SuppressWarnings("unchecked")
	public void testServletCorrectlyCalculatesPassesOnRequestPath() throws Exception
	{
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/emptytrader/css/common_chrome10_css.bundle");
		assertEquals("200", responseMap.get("responseCode"));

		verify(mockCssBundler).getBundleFiles(any(File.class), eq((File) null), eq("emptytrader/css/common_chrome10_css.bundle"));
		verify(mockCssBundler).writeBundle(any(List.class), any(OutputStream.class));
	}
	
	@Test
	public void testServletDoesntCallBundlerIfNotAbsoluteMatch() throws Exception
	{
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/xcss.bundle");
		assertEquals("400", responseMap.get("responseCode"));
		verify(mockCssBundler).getBundlerExtension();
		verify(mockCssBundler).getValidRequestForms();
		verifyNoMoreInteractions(mockCssBundler);
	}

	@Test @SuppressWarnings("unchecked")
	public void testServletPassesCallOntoCorrectBundlerWithNestedPath() throws Exception
	{
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/a/b/c/d/css.bundle");
		assertEquals("200", responseMap.get("responseCode"));

		verify(mockCssBundler).getBundleFiles(any(File.class), eq((File) null), eq("a/b/c/d/css.bundle"));
		verify(mockCssBundler).writeBundle(any(List.class), any(OutputStream.class));
	}

	@Test @SuppressWarnings("unchecked")
	public void testCorrectBaseDirIsPassedToBundler_jsBundler() throws Exception
	{
		Server appServer = createServer(8081, "/", tempSdkDir.getPath() + "/" + APPS + "/app1");
		makeRequest("http://localhost:8081/a-bladeset/blades/blade1/js/js.bundle");
		appServer.stop();
		
		verify(mockJsBundler).getBundleFiles(
				eq(new File(tempSdkDir.getPath(),  APPS + "/app1/a-bladeset/blades/blade1/").getCanonicalFile()), 
				eq((File) null), 
				eq("js/js.bundle"));
		verify(mockJsBundler).writeBundle(any(List.class), any(OutputStream.class));
	}
	
	@Test @SuppressWarnings("unchecked")
	public void testCorrectBaseDirIsPassedToBundler_xmlBundler() throws Exception
	{
		Server appServer = createServer(8081, "/", tempSdkDir.getPath() + "/" + APPS + "/app1");
		makeRequest("http://localhost:8081/a-bladeset/blades/blade1/xml.bundle");
		appServer.stop();
		
		verify(mockXmlBundler).getBundleFiles(
				eq(new File(tempSdkDir.getPath(),  APPS + "/app1/a-bladeset/blades/blade1/").getCanonicalFile().getCanonicalFile()), 
				eq((File) null), 
				eq("xml.bundle"));
		verify(mockXmlBundler).writeBundle(any(List.class), any(OutputStream.class));
	}
	
	@Test @SuppressWarnings("unchecked")
	public void testCorrectBaseDirIsPassedToBundler_cssBundler() throws Exception
	{
		Server appServer = createServer(8081, "/", tempSdkDir.getPath() + "/" + APPS + "/app1");
		makeRequest("http://localhost:8081/a-bladeset/blades/blade1/css/common_en_GB_css.bundle");
		appServer.stop();
		
		verify(mockCssBundler).getBundleFiles(
				eq(new File(tempSdkDir.getPath(),  APPS + "/app1/a-bladeset/blades/blade1/").getCanonicalFile()), 
				eq((File) null), 
				eq("css/common_en_GB_css.bundle"));
		verify(mockCssBundler).writeBundle(any(List.class), any(OutputStream.class));
	}

	@Test
	public void testServletRespondsWith400ErrorAndListsKnownBundlersIfNoBundlerFound() throws Exception
	{
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/a/b/c/d/123.bundle");
		assertEquals("400", responseMap.get("responseCode"));
		
		assertTrue(responseMap.get("responseText").contains("No bundler was found to handle the request. Valid bundler paths are:"));
	}

	@Test
	public void testExceptionThrownIfBasePathIsntValid() throws Exception
	{
		Server appServer = createServer(1234, "/", "src/test/resources1234");
		Map<String, String> responseMap = makeRequest("http://localhost:1234/a/b/c/d/css.bundle");
		assertEquals("400", responseMap.get("responseCode"));
		assertTrue(responseMap.get("responseText").contains("Error calculating root directory"));
		appServer.stop();
	}

	@Test
	public void testExceptionIsThrownIfBundlerThrowsMalformedUrlException() throws Exception
	{
		doThrow(new MalformedRequestException("http://localhost:"+PORT+"/a/b/c/d/css.bundle", "oops, error!")).when(mockCssBundler).getBundleFiles(any(File.class), eq((File) null), eq("a/b/c/d/css.bundle"));
		
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/a/b/c/d/css.bundle");

		verify(mockCssBundler).getBundleFiles(any(File.class), eq((File) null), eq("a/b/c/d/css.bundle"));
		// We don't get the other invocations due to the above method being called first.
		assertEquals("400", responseMap.get("responseCode"));
//		assertTrue(responseMap.get("responseText").contains("MalformedBundlerRequestException"));
		assertTrue(responseMap.get("responseText").contains("oops, error!"));
	}

	@Test @SuppressWarnings("unchecked")
	public void testExceptionIsThrownIfBundlerThrowsBundlerProcessingException() throws Exception
	{
		doThrow(
				new BundlerProcessingException("oops, error!")
		).when(mockCssBundler).writeBundle(any(List.class), any(OutputStream.class));
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/a/b/c/d/css.bundle");

		verify(mockCssBundler).getBundleFiles(any(File.class), eq((File) null), eq("a/b/c/d/css.bundle"));
		verify(mockCssBundler).writeBundle(any(List.class), any(OutputStream.class));
		assertEquals("500", responseMap.get("responseCode"));
// TODO: commented out by dominicc: we need a massive clean-up of bundler servlet exception handling anyway
//		assertTrue(responseMap.get("responseText").contains("BundlerProcessingException"));
		assertTrue(responseMap.get("responseText").contains("oops, error!"));
	}
	
	@Test @SuppressWarnings("unchecked")
	public void test404ResponseIsSentIfABundlerThrowsImageNotFoundException() throws Exception
	{
		doThrow(new ResourceNotFoundException()).when(mockImageBundler).writeBundle(any(List.class), any(OutputStream.class));
		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/a/b/c/d/wrongUrl.jpg_image.bundle");

		verify(mockImageBundler).getBundleFiles(any(File.class), eq((File) null), eq("a/b/c/d/wrongUrl.jpg_image.bundle"));
		verify(mockImageBundler).writeBundle(any(List.class), any(OutputStream.class));
		assertEquals("404", responseMap.get("responseCode"));
		assertTrue(responseMap.get("responseText").contains("Problem accessing /a/b/c/d/wrongUrl.jpg_image.bundle"));
// TODO: commented out by dominicc: we need a massive clean-up of bundler servlet exception handling anyway
//		assertTrue(responseMap.get("responseText").contains("oops, error!"));
	}

	@Test
	public void testBundlerCallWritesToResponse() throws Exception
	{

		LegacyFileBundlerPlugin dummyBundler = new LegacyFileBundlerPlugin()
		{
			@Override
			public void setBRJS(BRJS brjs)
			{	
			}
			
			@Override
			public String getBundlerExtension()
			{
				return "dummy.bundle";
			}
			
			@Override
			public List<String> getValidRequestForms()
			{
				return new ArrayList<String>();
			}
			
			@Override
			public List<File> getBundleFiles(File baseDir, File testDir, String requestName) throws RequestHandlingException
			{
				return null;
			}

			@Override
			public void writeBundle(List<File> sourceFiles, OutputStream outputStream) throws BundlerProcessingException
			{
				Writer writer = BundleWriterFactory.createWriter(outputStream);
				
				try
				{
					writer.append("Hello World!");
				}
				catch (IOException e)
				{
					throw new BundlerProcessingException(e, "Unable to write to output stream.");
				}
				finally
				{
					BundleWriterFactory.closeWriter(writer);
				}
			}

			@Override
			public List<String> getValidRequestStrings(AppMetaData appMetaData)
			{
				return null;
			}
		};
		bundlerServlet.bundlers = Arrays.asList(dummyBundler);

		Map<String, String> responseMap = makeRequest("http://localhost:"+PORT+"/a/b/c/d/dummy.bundle");
		assertEquals("200", responseMap.get("responseCode"));
		assertEquals("Hello World!", responseMap.get("responseText"));
	}

	/* helper methods */

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

	private Server createServer(int port, String contextPath, String resourceBase) throws Exception
	{
		Server appServer = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(contextPath);
		context.setResourceBase(resourceBase);
		context.addServlet(new ServletHolder(bundlerServlet), "/*");
		appServer.setHandler(context);
		appServer.start();
		return appServer;
	}

}
