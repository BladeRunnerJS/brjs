package org.bladerunnerjs.spec.brjs.appserver;

import java.net.ServerSocket;

import javax.servlet.Servlet;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
		
		given(brjs).hasServlets( new MockContentPlugin() )
			.and(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
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
	
	@Ignore
	@Test
	public void brjsServletIsAutomaticallyLoaded() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/brjs/version/", brjs.versionInfo().getVersionNumber());
	}
	
	@Ignore
	@Test
	public void brjsServletDoesntHandleAspectIndexFile() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFileWithContents("index.html", "aspect index.html")
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/default-aspect/index.html", "aspect index.html");
	}
	
	@Ignore
	@Test
	public void contentPluginsCanHandleRequests() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/default-aspect/mock-servlet", MockContentPlugin.class.getCanonicalName());
	}
	
	@Ignore
	@Test
	public void longUrlsDontGetHandedToOtherServlets() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app, helloWorldServlet, "/hello");
		then(appServer).requestForUrlReturns("/app/default-aspect/mock-servlet/some/other/path", MockContentPlugin.class.getCanonicalName())
			.and(appServer).requestForUrlReturns("/app/hello", "Hello World!");
	}
	
	@Ignore
	@Test
	public void brjsServletAllowsOtherServletsToBeAdded() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app, helloWorldServlet, "/hello/*");
		then(appServer).requestForUrlReturns("/app/hello", "Hello World!");
	}
	
	@Ignore
	@Test
	public void brjsServletAllowsOtherServletsToBeAddedWithExtensionMapping() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app, helloWorldServlet, "*.mock");
		then(appServer).requestForUrlReturns("/app/hello.mock", "Hello World!");
	}
	
	@Ignore
	@Test
	public void brjsServletHandsOffToBundlersAndMinifiers() throws Exception
	{
		given(app).hasBeenCreated()
			.and(blade).packageOfStyle("novox.cjs", "caplin-js")
    		.and(blade).packageOfStyle("novox.node", "node.js")
    		.and(blade).hasClasses("novox.cjs.Class", "novox.node.Class")
    		.and(aspect).indexPageRefersTo("novox.cjs.Class")
    		.and(blade).classDependsOn("novox.cjs.Class",  "novox.node.Class")
    		.and(appServer).started()
			.and(appServer).appHasServlet(app, helloWorldServlet, "/hello");
		when(appServer).requestIsMadeFor("/app/default-aspect/js/prod/en_GB/closure-whitespace/js.bundle", response);
		then(response).textEquals("novox.node.Class=function(){};var Class=require(\"novox/node/Class\");novox.cjs.Class=function(){};");
	}
	
}
