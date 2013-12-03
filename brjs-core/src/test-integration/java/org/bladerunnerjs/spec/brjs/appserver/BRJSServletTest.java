package org.bladerunnerjs.spec.brjs.appserver;

import java.net.ServerSocket;

import javax.servlet.Servlet;

import org.bladerunnerjs.core.plugin.bundlesource.js.NamespacedJsBundlerPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BRJSServletTest extends SpecTest
{

	ApplicationServer appServer;
	App app;
	Aspect aspect;
	Blade blade;
	DirNode appJars;
	ServerSocket socket;
	Servlet helloWorldServlet;
	StringBuffer response = new StringBuffer();

	@Before
	public void initTestObjects() throws Exception {
		
		given(brjs).hasContentPlugins( new MockContentPlugin() )
			.and(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated()
			.and(brjs).usedForServletModel();
    		appServer = brjs.applicationServer(appServerPort);
    		app = brjs.app("app");
    		aspect = app.aspect("default");
    		blade = app.bladeset("bs").blade("b1");
    		appJars = brjs.appJars();
    		appJars.create();
    		helloWorldServlet = new HelloWorldServlet();
	}
	
	
	@After
	public void stopServer() throws Exception
	{
		given(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs.applicationServer(appServerPort)).requestTimesOutFor("/");
		if (socket  != null && socket.isBound()) { socket.close(); }
	}
	
	@Test
	public void brjsServletIsAutomaticallyLoaded() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/brjs/version/", brjs.versionInfo().getVersionNumber());
	}
	
	@Test
	public void brjsServletDoesntHandleAspectIndexFile() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("index.html", "aspect index.html")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/default-aspect/index.html", "aspect index.html");
	}
	
	@Test
	public void contentPluginsCanHandleRequests() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/default-aspect/mock-servlet", MockContentPlugin.class.getCanonicalName());
	}
	
	@Test
	public void longUrlsDontGetHandedToOtherServlets() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app, helloWorldServlet, "/hello");
		then(appServer).requestForUrlReturns("/app/default-aspect/mock-servlet/some/other/path", MockContentPlugin.class.getCanonicalName())
			.and(appServer).requestForUrlReturns("/app/hello", "Hello World!");
	}
	
	@Test
	public void brjsServletAllowsOtherServletsToBeAdded() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app, helloWorldServlet, "/hello/*");
		then(appServer).requestForUrlReturns("/app/hello", "Hello World!");
	}
	
	@Test
	public void brjsServletAllowsOtherServletsToBeAddedWithExtensionMapping() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app, helloWorldServlet, "*.mock");
		then(appServer).requestForUrlReturns("/app/hello.mock", "Hello World!");
	}
	
	@Test
	public void brjsServletHandsOffToBundlersAndMinifiers() throws Exception
	{
		given(app).hasBeenCreated()
			.and(blade).hasPackageStyle("src/cjs", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(blade).hasPackageStyle("src/node", "node.js")
			.and(blade).hasClasses("cjs.Class", "node.Class")
			.and(aspect).indexPageRefersTo("cjs.Class")
			.and(blade).classRefersTo("cjs.Class",  "node.Class")
    		.and(appServer).started()
			.and(appServer).appHasServlet(app, helloWorldServlet, "/hello");
		when(appServer).requestIsMadeFor("/app/default-aspect/js/prod/en_GB/closure-whitespace/bundle.js", response);
		then(response).containsMinifiedClasses("cjs.Class", "node.Class");
	}
	
}
