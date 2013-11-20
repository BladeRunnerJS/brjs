package org.bladerunnerjs.spec.brjs.appserver;

import java.net.ServerSocket;

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
	StringBuffer response = new StringBuffer();

	@Before
	public void initTestObjects() throws Exception {
		
		given(brjs).hasServlets( new MockServletPlugin() )
			.and(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
    		appServer = brjs.applicationServer(appServerPort);
    		app = brjs.app("app");
    		aspect = app.aspect("default");
    		blade = app.bladeset("bs").blade("b1");
    		appJars = brjs.appJars();
    		appJars.create();
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
	public void servletPluginsCanHandleRequests() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started();
		then(appServer).requestForUrlReturns("/app/default-aspect/mock-servlet", MockServletPlugin.class.getCanonicalName());
	}
	
	@Test
	public void longUrlsDontGetHandedToOtherServlets() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app, new HelloWorldServlet(), "/hello");
		then(appServer).requestForUrlReturns("/app/default-aspect/mock-servlet/some/other/path", MockServletPlugin.class.getCanonicalName());
	}
	
	@Test
	public void brjsServletAllowsOtherServletsToBeAdded() throws Exception
	{
		given(app).hasBeenCreated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app, new HelloWorldServlet(), "/hello");
		then(appServer).requestForUrlReturns("/app/hello", "Hello World!");
	}
	
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
			.and(appServer).appHasServlet(app, new HelloWorldServlet(), "/hello");
		when(appServer).requestIsMadeFor("/app/default-aspect/js/prod/en_GB/closure-whitespace/js.bundle", response);
		then(response).textEquals("novox.node.Class=function(){};var Class=require(\"novox/node/Class\");novox.cjs.Class=function(){};");
	}
	
}
