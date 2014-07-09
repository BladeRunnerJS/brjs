package org.bladerunnerjs.spec.brjs.appserver;


import java.net.ServerSocket;

import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
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
	Workbench workbench;
	DirNode appJars;
	ServerSocket socket;
	StringBuffer response = new StringBuffer();
	DirNode sdkLibsDir;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsAssetLocationPlugins()
			.and(brjs).automaticallyFindsContentPlugins()
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
			aspect = app.aspect("default");
			systemAspect = systemApp.aspect("default");
			blade = app.bladeset("bs").blade("b1");
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
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("index.jsp", "<%= 1 + 2 %>")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "3");
	}
	
	@Test
	public void indexPageCanBeAccessed() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("index.html", "aspect index.html")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "aspect index.html");
	}
	
	@Ignore // Failure test case for #712
	@Test
	public void indexPageCanBeAccessedWithoutEndingInForwardSlashAfterLocale() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("index.html", "aspect index.html")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en", "aspect index.html");
	}
	
	@Test
	public void requestsForInvalidModelPathsThatDoExistOnDiskReturn404() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("index.html", "aspect index.html")
			.and(appServer).started();
		then(appServer).requestCannotBeMadeFor("/app/default-aspect/index.html");
		/* The correct URL is /app/en/index.html but /app/default-aspect/index.html is a valid path on disk. 
		 	All requests should go through the model so verify the invalid model request returns a 404 and is not served from disk. */
	}
	
	@Test
	public void jspIndexPageCanBeAccessed() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("index.jsp", "<%= \"aspect \" + \"index.jsp\" %>")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "aspect index.jsp");
	}
	
	@Test
	public void contentPluginsCanHandleRequests() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/v/123/mock-content-plugin/", MockContentPlugin.class.getCanonicalName());
	}
	
	@Test
	public void contentPluginsCanHandleRequestsWithinWorkbenches() throws Exception {
		given(app).hasBeenPopulated()
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/workbench/bs/b1/v/123/mock-content-plugin/", MockContentPlugin.class.getCanonicalName());
	}
	
	@Test
	public void webInfFolderDoesntHaveToBePresentToEnableBrjsFeatures() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("index.html", "<@tagToken @/>");
		when(app).fileDeleted("WEB-INF")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/v/123/mock-content-plugin/", MockContentPlugin.class.getCanonicalName())
			.and(appServer).requestForUrlReturns("/app/en/", "dev replacement")
			.and(app).doesNotHaveDir("WEB-INF");
	}
	
	@Test
	public void indexPageCanBeAccessedWithAQueryString() throws Exception {
		given(app).hasBeenPopulated()
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
		given(app).hasBeenPopulated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app, new HelloWorldServlet(), "/servlet/hello");
		then(appServer).requestForUrlReturns("/app/v/123/mock-content-plugin/some/other/path/", MockContentPlugin.class.getCanonicalName())
			.and(appServer).requestForUrlReturns("/app/servlet/hello", "Hello World!");
	}
	
	@Test
	public void systemAppsCanBeServed() throws Exception
	{
		given(systemApp).hasBeenPopulated()
			.and(systemAspect).containsFileWithContents("index.html", "System App")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "System App");
	}
	
	@Test
	public void userAppsTakePriorityOverSystemApps() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(systemApp).hasBeenPopulated()
			.and(aspect).containsFileWithContents("index.html", "User App")
			.and(systemAspect).containsFileWithContents("index.html", "System App")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/en/", "User App");
	}
	
	@Test
	public void jspsAreParsed() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("unbundled-resources/file.jsp", "<%= 1 + 2 %>")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/v/123/unbundled-resources/file.jsp", "3");
	}
	
	@Test
	public void jspsCanHaveQueryStrings() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("unbundled-resources/file.jsp", "<%= request.getParameter(\"query\") + \" \" + request.getParameter(\"debug\")  %>")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/v/123/unbundled-resources/file.jsp?query=1337&debug=true", "1337 true");
	}
	
	@Test
	public void jspsCanSendRedirects() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(aspect).containsFileWithContents("unbundled-resources/file.jsp", "<% response.sendRedirect(\"/\");  %>")
			.and(appServer).started();
		then(appServer).requestForUrlHasResponseCode("/app/v/123/unbundled-resources/file.jsp", 302)
			.and(appServer).requestIsRedirected("/app/v/123/unbundled-resources/file.jsp", "/");
	}
	
	@Test
	public void jspsCanSend404() throws Exception
	{
		given(app).hasBeenPopulated()
		.and(aspect).containsFileWithContents("unbundled-resources/file.jsp", "<% response.sendError(404);  %>")
		.and(appServer).started();
		then(appServer).requestForUrlHasResponseCode("/app/v/123/unbundled-resources/file.jsp", 404);
	}

	@Test
	public void contentPluginsCanDefineNonVersionedUrls() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/static/mock-content-plugin/unversioned/url", MockContentPlugin.class.getCanonicalName());
	}
}
