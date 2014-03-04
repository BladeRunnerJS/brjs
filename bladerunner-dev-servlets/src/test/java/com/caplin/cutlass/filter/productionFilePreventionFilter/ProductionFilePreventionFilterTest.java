package com.caplin.cutlass.filter.productionFilePreventionFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.caplin.cutlass.ServletModelAccessor;

public class ProductionFilePreventionFilterTest
{
	private static final String APP_DIR = "src/test/resources/ProductionFilePreventionFilter/app1";
	
	private ProductionFilePreventionFilter filter;
	private Server appServer;
	private DefaultHttpClient httpclient;

	@Before
	public void setup() throws Exception
	{
		ServletModelAccessor.destroy();
		this.filter = new ProductionFilePreventionFilter();
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

	@Test
	public void testImpliedIndexHtmlIsAcceptable()
	{
		assertTrue("/app1/", filter.isAcceptableRequest("/main-aspect/"));
	}

	@Test
	public void testIndexHtmlIsAcceptable()
	{
		assertTrue("/app1/main-aspect/", filter.isAcceptableRequest("/index.html"));
	}

	@Test
	public void testXmlBundleIsAcceptable()
	{
		assertTrue("/app1/main-aspect/", filter.isAcceptableRequest("/xml.bundle"));
	}

	@Test
	public void testBundleWithIncorrectExtensionIsNotAcceptable()
	{
		assertFalse(filter.isAcceptableRequest("/bundles/secret.bundle.package"));
	}

	@Test
	public void testPngFileIsNotAcceptable()
	{
		assertFalse("/app1/main-aspect/", filter.isAcceptableRequest("/icon.png"));
	}

	@Test
	public void testPngFileInUnbundledResourceIsAcceptable()
	{
		assertTrue("/app1/main-aspect/", filter.isAcceptableRequest("/unbundled-resources/icon.png"));
	}
	
	@Test
	public void testAccessToServletIsAcceptable()
	{
		assertTrue("/app1/main-aspect/", filter.isAcceptableRequest("/servlet/XHRKeymaster"));
		assertTrue("/app1/main-aspect/", filter.isAcceptableRequest("/servlet/StandardKeymaster"));
		assertTrue("/app1/main-aspect/", filter.isAcceptableRequest("/servlet/Poll"));
		assertTrue("/app1/main-aspect/", filter.isAcceptableRequest("/servlet/webcentric/abc"));
	}

	@Test
	public void testFilterAllowsAccessToIndexHtml() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/index.html"));

		assertEquals(200, response.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testFilterAllowsAccessToIndexHtmlContent() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();
		
		String content = httpclient.execute(new HttpGet("http://localhost:7357/app1/index.html"), new BasicResponseHandler());
		
		assertEquals("app1/index.html", content);
	}

	@Test
	public void testFilterAllowsAccessToCssBundle() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/bundles/css.bundle"));

		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void testFilterAllowsAccessToHtmlBundle() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/bundles/html.bundle"));

		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void testFilterAllowsAccessToI18nBundle() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/bundles/i18n.bundle"));

		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void testFilterAllowsAccessToJsBundle() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/bundles/my-js.bundle"));

		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void testFilterAllowsAccessToXmlBundle() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/bundles/xml.bundle"));

		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void testFilterGives404WhenRequestingBundleThatDoesNotExist() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/bundles/fake.bundle"));

		assertEquals(404, response.getStatusLine().getStatusCode());
	}

	@Test
	public void testFilterBlocksAccessToTxtFile() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/main-aspect/file.txt"));

		assertEquals(404, response.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testFilterBlocksAccessToTxt2File() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();
		
		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/main-aspect/themes/blue/file.txt"));
		
		assertEquals(404, response.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testFilterBlocksAccessToCssFile() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();
		
		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/main-aspect/themes/blue/blue.css"));
		
		assertEquals(404, response.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testFilterAllowsAccessToUnbundledResources() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();
		
		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/main-aspect/unbundled-resources/liberator-service-configuration.xml"));
		
		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void testFilterAllowsAccessToBundleInASection() throws Exception
	{
		appServer = createServer();
		httpclient = new DefaultHttpClient();

		HttpResponse response = httpclient.execute(new HttpGet("http://localhost:7357/app1/main-aspect/html.bundle"));

		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void testFilterPerformsRedirectAfterAllOtherFiltersHaveRun() throws Exception
	{
		FilterConfig mockConfig = mock(FilterConfig.class);
		ServletContext mockContext = mock(ServletContext.class);

		when(mockConfig.getServletContext()).thenReturn(mockContext);
		when(mockContext.getRealPath("/")).thenReturn((new File(".")).getAbsolutePath());

		ProductionFilePreventionFilter filter = new ProductionFilePreventionFilter();
		filter.init(mockConfig);

		final HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getRequestURI()).thenReturn("/app1/bundles/bundle.xml");

		HttpServletResponse mockResponse = mock(HttpServletResponse.class);
		when(mockResponse.getOutputStream()).thenReturn(mock(ServletOutputStream.class));

		FilterChain mockChain = mock(FilterChain.class);
		doAnswer(new Answer()
		{
			public Object answer(InvocationOnMock invocation)
			{
				reset(mockRequest);
				when(mockRequest.getRequestURI()).thenReturn("/app1/bundles/xml.bundle");
				return null;
			}
		}).when(mockChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));

		filter.doFilter(mockRequest, mockResponse, mockChain);
		verify(mockResponse, never()).sendError(404);
	}

	private Server createServer() throws Exception
	{
		Server appServer = new Server(7357);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/app1");
		context.setResourceBase(APP_DIR);
		context.addFilter(new FilterHolder(filter), "/*", null);
		context.addServlet(DefaultServlet.class, "/*");
		appServer.setHandler(context);
		appServer.start();
		
		return appServer;
	}
}
