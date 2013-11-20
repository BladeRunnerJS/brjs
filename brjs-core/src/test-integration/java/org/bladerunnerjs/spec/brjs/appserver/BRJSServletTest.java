package org.bladerunnerjs.spec.brjs.appserver;

import java.net.ServerSocket;

import org.bladerunnerjs.core.plugin.servlet.ServletPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BRJSServletTest extends SpecTest
{

	ApplicationServer appServer;
	App app1;
	DirNode appJars;
	ServerSocket socket;
	StringBuffer response = new StringBuffer();
	
	ServletPlugin mockServletPlugin;

	@Before
	public void initTestObjects() throws Exception {
		
		given(brjs).hasServlets( new MockServletPlugin() )
			.and(brjs).hasBeenCreated();
    		appServer = brjs.applicationServer(appServerPort);
    		app1 = brjs.app("app");
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
		given(app1).hasBeenCreated();
		when(appServer).started();
		then(appServer).requestForUrlReturns("/app/brjs/version/", brjs.versionInfo().getVersionNumber());
	}
	
	@Test
	public void servletPluginsCanHandleRequests() throws Exception
	{
		given(app1).hasBeenCreated();
		when(appServer).started();
		then(appServer).requestForUrlReturns("/app/mock-servlet", MockServletPlugin.class.getCanonicalName());
	}
	
}
