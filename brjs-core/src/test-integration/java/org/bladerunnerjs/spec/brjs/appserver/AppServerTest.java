package org.bladerunnerjs.spec.brjs.appserver;

import static org.bladerunnerjs.appserver.BRJSApplicationServer.Messages.*;
import static org.bladerunnerjs.appserver.ApplicationServerUtils.Messages.*;

import java.io.IOException;
import java.net.ServerSocket;

import javax.servlet.Servlet;

import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.appserver.BRJSApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.events.NodeReadyEvent;
import org.bladerunnerjs.plugin.plugins.appdeployer.AppDeploymentObserverPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class AppServerTest extends SpecTest
{
	BRJS secondBrjsProcess;
	
	ApplicationServer appServer;
	App sysapp1;
	App sysapp2;
	App app1;
	App app2;
	DirNode appJars;
	ServerSocket socket;
	Servlet helloWorldServlet;

	@Before
	public void initTestObjects() throws Exception {
		given(brjs).hasModelObserverPlugins(new AppDeploymentObserverPlugin())
			.and(brjs).hasContentPlugins(new MockContentPlugin())
			.and(brjs).hasBeenCreated()
			.and(brjs).localeForwarderHasContents("locale-forwarder.js")
			.and(brjs).containsFolder("apps")
			.and(brjs).containsFolder("sdk/system-applications");
			appServer = brjs.applicationServer(appServerPort);
			app1 = brjs.app("app1");
			app2 = brjs.app("app2");
			sysapp1 = brjs.systemApp("sysapp1");
			sysapp2 = brjs.systemApp("sysapp2");
			appJars = brjs.appJars();
			appJars.create();
		
		secondBrjsProcess = createNonTestModel();
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
	public void appIsNotHostedUnlessAppIsDeployed() throws Exception
	{
		given(appServer).started();
		when(app1).create();
		then(appServer).requestCannotBeMadeFor("/app1");
	}
	
	@Test
	public void appIsDeployedWhenAppServerStarts() throws Exception
	{
		given(logging).enabled()
			.and(app1).hasBeenCreated();
		when(appServer).started();
		then(appServer).requestCanBeMadeFor("/app1")
			.and(appServer).requestIs302Redirected("/","/dashboard")
			.and(logging).infoMessageReceived(SERVER_STARTING_LOG_MSG, "BladeRunnerJS")
			.and(logging).infoMessageReceived(SERVER_STARTED_LOG_MESSAGE, appServerPort)
			.and(logging).debugMessageReceived(DEPLOYING_APP_MSG, "app1");
	}
	
	@Test
	public void multipleAppsAreHostedWhenAppServerStarts() throws Exception
	{
		given(app1).hasBeenCreated()
			.and(app2).hasBeenCreated();
		when(appServer).started();
		then(appServer).requestCanBeMadeFor("/app1")
			.and(appServer).requestCanBeMadeFor("/app2");
	}
	
	@Test
	public void newAppsAreAutomaticallyHosted() throws Exception
	{
		given(appServer).started();
		when(app1).populate()
			.and(app1).deployApp();
		then(appServer).requestCanEventuallyBeMadeFor("/app1");
	}	
	
	@Test
	public void appDeploymentObserverIsAutomaticallyLoaded() throws Exception
	{
		when(app1).populate()
			.and(app1).deployApp();
		then(app1).hasFile(".deploy");
	}	
	
	@Test
	public void newAppsAreOnlyHostedOnAppDeployedEvent() throws Exception
	{
		given(appServer).started();
		when(app1).populate()
			.and(brjs).eventFires(new NodeReadyEvent(), app1);
		then(appServer).requestCannotBeMadeFor("/app1/default-aspect/index.html");
	}
	
	@Test
	public void exceptionIsThrownIfAppserverIsStartedOnBoundPort() throws Exception
	{
		socket = new ServerSocket(appServer.getPort());
		
		when(appServer).started();
		then(exceptions).verifyFormattedException( IOException.class, BRJSApplicationServer.Messages.PORT_ALREADY_BOUND_EXCEPTION_MSG, appServer.getPort(), BRJS.PRODUCT_NAME );
	}
	
	@Test
	public void singleSystemAppCanBeHosted() throws Exception
	{
		given(sysapp1).hasBeenCreated();
		when(appServer).started();
		then(appServer).requestCanBeMadeFor("/sysapp1");
	}
	
	@Test
	public void multipleSystemAppsCanBeHosted() throws Exception
	{
		given(sysapp1).hasBeenCreated()
			.and(sysapp2).hasBeenCreated();
		when(appServer).started();
		then(appServer).requestCanBeMadeFor("/sysapp1")
			.and(appServer).requestCanBeMadeFor("/sysapp2");
	}
	
	@Test
	public void systemAppIsAutomaticallyHostedOnDeploy() throws Exception
	{
		given(appServer).started();
		when(sysapp1).populate()
			.and(sysapp1).deployApp();
		then(appServer).requestCanEventuallyBeMadeFor("/sysapp1");
	}
	
	@Test
	public void rootContextRedirectsToDashboard() throws Exception
	{
		given(appServer).started();
		then(appServer).requestIs302Redirected("/","/dashboard");
	}
	
	@Test
	public void invalidUrlReturns404() throws Exception
	{
		given(appServer).started();
		then(appServer).requestCannotBeMadeFor("/some-invalid-url");
	}
	
	@Test
	public void otherServletsCanBeAddedWithRootMapping() throws Exception
	{
		given(brjs).usedForServletModel()
			.and(app1).hasBeenPopulated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app1, helloWorldServlet, "/servlet/hello/*");
		then(appServer).requestForUrlReturns("/app1/servlet/hello", "Hello World!");
	}
	
	@Test
	public void otherServletsCanBeAddedWithExtensionMapping() throws Exception
	{
		given(brjs).usedForServletModel()
			.and(app1).hasBeenPopulated()
			.and(appServer).started()
			.and(appServer).appHasServlet(app1, helloWorldServlet, "*.mock");
		then(appServer).requestForUrlReturns("/app1/hello.mock", "Hello World!");
	}
	
	@Test
	public void newAppsAreAutomaticallyHostedWhenRunningCreateAppCommandFromADifferentModelInstance() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(brjs.applicationServer(appServerPort)).started();
		when(secondBrjsProcess).runCommand("create-app", "app1", "blah");
		then(appServer).requestCanEventuallyBeMadeFor("/app1/");
	}
	
	@Test
	public void newAppsAreHostedOnAppserverAfterServerRestart() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(brjs.applicationServer(appServerPort)).started();
		when(secondBrjsProcess).runCommand("create-app", "app1", "blah")
			.and(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs.applicationServer(appServerPort)).started();
		then(appServer).requestCanEventuallyBeMadeFor("/app1/");
	}
	
	@Test
	public void newAppsAreHostedViaADifferentModelOnAppserverAfterServerRestart() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(brjs.applicationServer(appServerPort)).started();
		when(secondBrjsProcess).runCommand("create-app", "app1", "blah")
			.and(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs).hasBeenAuthenticallyReCreated()
			.and(brjs.applicationServer(appServerPort)).started();
		then(appServer).requestCanEventuallyBeMadeFor("/app1/");
	}
}
