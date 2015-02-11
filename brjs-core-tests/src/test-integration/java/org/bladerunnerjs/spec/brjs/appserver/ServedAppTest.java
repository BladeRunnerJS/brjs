package org.bladerunnerjs.spec.brjs.appserver;


import java.net.ServerSocket;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.BladeWorkbench;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class ServedAppTest extends SpecTest
{
	ApplicationServer appServer;
	App app;
	App systemApp;
	Aspect aspect;
	Aspect systemAspect;
	Blade blade;
	BladeWorkbench workbench;
	DirNode appJars;
	ServerSocket socket;
	StringBuffer response = new StringBuffer();
	DirNode sdkLibsDir;
	private Aspect anotherAspect;
	private Bladeset bladeset;
	private Aspect defaultAspect;
	private App appWithDefaultAspect;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsAssetLocationPlugins()
			.and(brjs).automaticallyFindsContentPlugins()
			.and(brjs).automaticallyFindsAssetPlugins()
			.and(brjs).automaticallyFindsRequirePlugins()
			.and(brjs).hasContentPlugins(new MockContentPlugin())
			.and(brjs).hasTagHandlerPlugins(new MockTagHandler("tagToken", "dev replacement", "prod replacement"))
			.and(brjs).hasBeenCreated()
			.and(brjs).usedForServletModel()
			.and(brjs).containsFolder("apps")
			.and(brjs).containsFolder("sdk/system-applications")
			.and(brjs).usesProductionTemplates()
			.and(brjs).hasDevVersion("123");
			appServer = brjs.applicationServer(appServerPort);
			app = brjs.userApp("app");
			systemApp = brjs.systemApp("app");
			aspect = app.defaultAspect();
			appWithDefaultAspect = brjs.app("anotherApp");
			defaultAspect = appWithDefaultAspect.defaultAspect();
			anotherAspect = app.aspect("another");
			systemAspect = systemApp.defaultAspect();
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
			appJars = brjs.appJars();
			appJars.create();
			sdkLibsDir = brjs.sdkJsLibsDir();
	}
	
	@After
	public void stopServer() throws Exception
	{
		given(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs.applicationServer(appServerPort)).requestTimesOutFor("/");
		if (socket  != null && socket.isBound()) { socket.close(); }
	}
	
	@Test
	public void jspSupportIsEnabled() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("index.jsp", "<%= 1 + 2 %>")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "3");
	}
	
	@Test
	public void indexPageCanBeAccessed() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("index.html", "aspect index.html")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "aspect index.html");
	}
	
	@Test
	public void localeForwarderPageCanBeAccessedWithoutEndingInForwardSlash() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(appServer).started();
		then(appServer).requestIs302Redirected("/app", "/app/");
	}
	
	@Test
	public void indexPageCanBeAccessedWithoutEndingInForwardSlash() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("index.html", "aspect index.html")
			.and(appServer).started();
		then(appServer).requestIs302Redirected("/app/en", "/app/en/");
	}
	
	@Test
	public void localeForwarderPageOfANonDefaultAspectCanBeAccessedWithoutEndingInForwardSlash() throws Exception
	{
		given(app).hasBeenPopulated("default")
    		.and(anotherAspect).hasBeenPopulated()
			.and(appServer).started();
		then(appServer).requestIs302Redirected("/app/another", "/app/another/");
	}
	
	@Test
	public void indexPageOfANonDefaultAspectCanBeAccessedWithoutEndingInForwardSlashAfterLocale() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(anotherAspect).containsFileWithContents("index.html", "aspect index.html")
			.and(appServer).started();
		then(appServer).requestIs302Redirected("/app/another/en", "/app/another/en/");
	}
	
	@Test
	public void localeRequestsAreOnlyRedirectedIfTheyAreValidModelRequests() throws Exception
	{
		given(app).hasBeenPopulated("default")
    		.and(anotherAspect).containsFileWithContents("index.html", "aspect index.html")
    		.and(appServer).started()
    		.and(appServer).appHasServlet(app, new HelloWorldServlet(), "/my_servlet/*");
		then(appServer).requestForUrlReturns("/app/my_servlet/en", "Hello World!");
	}
	
	@Test
	public void workbenchLocaleForwarderPageCanBeAccessedWithoutEndingInForwardSlash() throws Exception
	{
		given(app).hasBeenPopulated("default")
    		.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
    		.and(workbench).hasBeenCreated()
			.and(brjs).localeForwarderHasContents("locale forwarder")
			.and(appServer).started();
		then(appServer).requestIs302Redirected("/app/bs/b1/workbench", "/app/bs/b1/workbench/");
	}
	
	@Test
	public void workbenchIndexPageCanBeAccessedWithoutEndingInForwardSlashAfterLocale() throws Exception
	{
		given(app).hasBeenPopulated("default")
    		.and(bladeset).hasBeenCreated()
    		.and(blade).hasBeenCreated()
    		.and(workbench).hasBeenCreated()
    		.and(brjs).localeForwarderHasContents("locale forwarder")
    		.and(appServer).started();
    	then(appServer).requestIs302Redirected("/app/bs/b1/workbench/en", "/app/bs/b1/workbench/en/");
	}
	
	@Test
	public void requestsForInvalidModelPathsThatDoExistOnDiskReturn404() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("index.html", "aspect index.html")
			.and(appServer).started();
		then(appServer).requestCannotBeMadeFor("/app/default-aspect/index.html");
		/* The correct URL is /app/en/index.html but /app/default-aspect/index.html is a valid path on disk. 
		 	All requests should go through the model so verify the invalid model request returns a 404 and is not served from disk. */
	}
	
	@Test
	public void jspIndexPageCanBeAccessed() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("index.jsp", "<%= \"aspect \" + \"index.jsp\" %>")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "aspect index.jsp");
	}
	
	@Test
	public void contentPluginsCanHandleRequests() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/v/123/mock-content-plugin/", MockContentPlugin.class.getCanonicalName());
	}
	
	@Test
	public void contentPluginsCanHandleRequestsWithinWorkbenches() throws Exception {
		given(app).hasBeenPopulated("default")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/bs/b1/workbench/v/123/mock-content-plugin/", MockContentPlugin.class.getCanonicalName());
	}
	
	@Test
	public void webInfFolderDoesntHaveToBePresentToEnableBrjsFeatures() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("index.html", "<@tagToken @/>")
			.and(aspect).doesNotContainFile("WEB-INF");
		when(appServer).started();
		then(appServer).requestForUrlReturns("/app/v/123/mock-content-plugin/", MockContentPlugin.class.getCanonicalName())
			.and(appServer).requestForUrlReturns("/app/en/", "dev replacement")
			.and(app).doesNotHaveDir("WEB-INF");
	}
	
	@Test
	public void indexPageCanBeAccessedWithAQueryString() throws Exception {
		given(app).hasBeenPopulated("default")
			.and(appServer).started()
			.and(aspect).indexPageHasContent("index page")
			.and(brjs).localeForwarderHasContents("locale-forwarder.js");
		when(appServer).requestIsMadeFor("/app/en/?query=1", response);
		then(response).textEquals("index page");
	}
	
	@Test @Ignore
	public void bladeRunnerJSDoesntBreakAuthentication() {
		// TODO
	}
	
	@Test
	public void longUrlsDontGetHandedToOtherServlets() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(appServer).started()
			.and(appServer).appHasServlet(app, new HelloWorldServlet(), "/servlet/hello");
		then(appServer).requestForUrlReturns("/app/v/123/mock-content-plugin/some/other/path/", MockContentPlugin.class.getCanonicalName())
			.and(appServer).requestForUrlReturns("/app/servlet/hello", "Hello World!");
	}
	
	@Test
	public void systemAppsCanBeServed() throws Exception
	{
		given(systemApp).hasBeenPopulated("default")
			.and(systemAspect).containsFileWithContents("index.html", "System App")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "System App");
	}
	
	@Test
	public void systemAppsTakePriorityOverUserApps() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(systemApp).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("index.html", "User App")
			.and(systemAspect).containsFileWithContents("index.html", "System App")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "System App");
	}
	
	@Test
	public void jspsAreParsed() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("unbundled-resources/file.jsp", "<%= 1 + 2 %>")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/v/123/unbundled-resources/file.jsp", "3");
	}
	
	@Test
	public void jspsCanHaveQueryStrings() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("unbundled-resources/file.jsp", "<%= request.getParameter(\"query\") + \" \" + request.getParameter(\"debug\")  %>")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/v/123/unbundled-resources/file.jsp?query=1337&debug=true", "1337 true");
	}
	
	@Test
	public void jspsCanSendRedirects() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("unbundled-resources/file.jsp", "<% response.sendRedirect(\"/\");  %>")
			.and(appServer).started();
		then(appServer).requestForUrlHasResponseCode("/app/v/123/unbundled-resources/file.jsp", 302)
			.and(appServer).requestIs302Redirected("/app/v/123/unbundled-resources/file.jsp", "/");
	}
	
	@Test
	public void jspsCanSend404() throws Exception
	{
		given(app).hasBeenPopulated("default")
		.and(aspect).containsFileWithContents("unbundled-resources/file.jsp", "<% response.sendError(404);  %>")
		.and(appServer).started();
		then(appServer).requestForUrlHasResponseCode("/app/v/123/unbundled-resources/file.jsp", 404);
	}

	@Test
	public void contentPluginsCanDefineNonVersionedUrls() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/mock-content-plugin/unversioned/url", MockContentPlugin.class.getCanonicalName());
	}
	
	@Test
	public void unbundledResourcesCanBeUnversioned() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("unbundled-resources/file.txt", "unbundled resources file content")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/unbundled-resources/file.txt", "unbundled resources file content");
	}
	
	@Test
	public void optionalAspectCanBeUsedAsDefaultAspect() throws Exception
	{
		given(appWithDefaultAspect).hasBeenCreated()
			.and(defaultAspect).containsFileWithContents("index.html", "aspect index.html")
			.and(brjs).localeForwarderHasContents("locale forwarder")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/anotherApp/en/", "aspect index.html");
	}
	
	@Test
	public void correctContentLengthHeaderIsSetWhenTagsAreReplaced() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("index.html", "<@tagToken @/>");
		when(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "dev replacement")
			.and(appServer).contentLengthForRequestIs("/app/en/", "dev replacement".getBytes().length);
	}
	
	@Test
	public void jndiTokensAreReplaced() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("index.html", "@SOME.TOKEN@")
			.and(app).containsFileWithContents("WEB-INF/jetty-env.xml", ""
					+ "<?xml version=\"1.0\"?>"
					+ "<!DOCTYPE Configure PUBLIC \"-//Jetty//Configure//EN\" \"http://www.eclipse.org/jetty/configure.dtd\">"
					+ "<Configure id=\"webAppCtx\" class=\"org.eclipse.jetty.webapp.WebAppContext\">"
					+ "	<New class=\"org.eclipse.jetty.plus.jndi.EnvEntry\">"
					+ "		<Arg><Ref id='webAppCtx'/></Arg>"
					+ "		<Arg>SOME.TOKEN</Arg>"
					+ "		<Arg type=\"java.lang.String\">some token replacement</Arg>"
					+ "		<Arg type=\"boolean\">true</Arg>"
					+ "	</New>"
					+ "</Configure>");
		when(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "some token replacement");
	}
	
	@Test
	public void correctContentLengthIsSetWhenJNDITokensAreReplaced() throws Exception
	{
		given(app).hasBeenPopulated("default")
			.and(aspect).containsFileWithContents("index.html", "@SOME.TOKEN@")
			.and(app).containsFileWithContents("WEB-INF/jetty-env.xml", ""
					+ "<?xml version=\"1.0\"?>"
					+ "<!DOCTYPE Configure PUBLIC \"-//Jetty//Configure//EN\" \"http://www.eclipse.org/jetty/configure.dtd\">"
					+ "<Configure id=\"webAppCtx\" class=\"org.eclipse.jetty.webapp.WebAppContext\">"
					+ "	<New class=\"org.eclipse.jetty.plus.jndi.EnvEntry\">"
					+ "		<Arg><Ref id='webAppCtx'/></Arg>"
					+ "		<Arg>SOME.TOKEN</Arg>"
					+ "		<Arg type=\"java.lang.String\">some token replacement</Arg>"
					+ "		<Arg type=\"boolean\">true</Arg>"
					+ "	</New>"
					+ "</Configure>");
		when(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "some token replacement")
			.and(appServer).contentLengthForRequestIs("/app/en/", "some token replacement".getBytes().length);
	}
	
}
