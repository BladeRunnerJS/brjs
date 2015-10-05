package org.bladerunnerjs.spec.brjs.appserver;

import static org.bladerunnerjs.appserver.BRJSApplicationServer.Messages.*;
import static org.bladerunnerjs.appserver.ApplicationServerUtils.Messages.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import javax.servlet.Servlet;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.model.events.NodeReadyEvent;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.appserver.BRJSApplicationServer;
import org.bladerunnerjs.api.DirNode;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.plugin.appdeployer.AppDeploymentObserverPlugin;
import org.bladerunnerjs.utility.FileUtils;
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
	AppConf app1Conf;
	AppConf app2Conf;
	AppConf sysapp1Conf;
	AppConf sysapp2Conf;
	DirNode appJars;
	ServerSocket socket;
	Servlet helloWorldServlet;
	TemplateGroup templates;
	Aspect aspect;
	StringBuffer response = new StringBuffer();
	File secondaryTempFolder;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasModelObserverPlugins(new AppDeploymentObserverPlugin())
			.and(brjs).hasContentPlugins(new MockContentPlugin())
			.and(brjs).hasBeenCreated()
			.and(brjs).localeSwitcherHasContents("locale-forwarder.js")
			.and(brjs).containsFolder("apps")
			.and(brjs).containsFolder("sdk/system-applications");
			brjs.bladerunnerConf().setJettyPort(appServerPort);
			brjs.bladerunnerConf().write();
			appServer = brjs.applicationServer(appServerPort);
			app1 = brjs.app("app1");
			app2 = brjs.app("app2");
			aspect = app1.defaultAspect();
			app1Conf = app1.appConf();
			app2Conf = app2.appConf();
			templates = brjs.sdkTemplateGroup("default");
			sysapp1 = brjs.systemApp("sysapp1");
			sysapp2 = brjs.systemApp("sysapp2");
			sysapp1Conf = sysapp1.appConf();
			sysapp2Conf = sysapp2.appConf();
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
		if (secondaryTempFolder != null) org.apache.commons.io.FileUtils.deleteQuietly(secondaryTempFolder);
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
			.and(app1).hasBeenCreated()
			.and(app1Conf).supportsLocales("en", "de");
		when(appServer).started();
		then(appServer).requestCanBeMadeFor("/app1")
			.and(appServer).requestIs302Redirected("/","/dashboard")
			.and(logging).infoMessageReceived(SERVER_STARTING_LOG_MSG, "BladeRunnerJS")
			.and(logging).infoMessageReceived(BRJS.Messages.NO_APPS_DISCOVERED, "system")
			.and(logging).infoMessageReceived(BRJS.Messages.APPS_DISCOVERED, "User", "app1")
			.and(logging).infoMessageReceived(SERVER_STARTED_LOG_MESSAGE, appServerPort)
			.and(logging).debugMessageReceived(DEPLOYING_APP_MSG, "app1");
	}
	
	@Test
	public void multipleAppsAreHostedWhenAppServerStarts() throws Exception
	{
		given(app1).hasBeenCreated()
			.and(app1Conf).supportsLocales("en", "de")
			.and(app2).hasBeenCreated()
			.and(app2Conf).supportsLocales("en", "de");
		when(appServer).started();
		then(appServer).requestCanBeMadeFor("/app1")
			.and(appServer).requestCanBeMadeFor("/app2");
	}
	
	@Test
	public void newAppsAreAutomaticallyHosted() throws Exception
	{
		given(appServer).started()
			.and(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt");
		when(app1).populate("default")
			.and(app1Conf).localesUpdatedTo("en", "de")
			.and(app1).deployApp();
		then(appServer).requestCanEventuallyBeMadeFor("/app1");
	}	
	
	@Test
	public void deployFileIsOnlyCreatedIfAppServerIsStarted() throws Exception
	{
		given(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt");
		when(app1).populate("default")
			.and(app1).deployApp();
		then(app1).doesNotHaveFile(".deploy");
	}
	
	@Test
	public void newAppsAreOnlyHostedOnAppDeployedEvent() throws Exception
	{
		given(appServer).started()
			.and(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt");
		when(app1).populate("default")
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
		given(sysapp1).hasBeenCreated()
			.and(sysapp1Conf).supportsLocales("en", "de");
		when(appServer).started();
		then(appServer).requestCanBeMadeFor("/sysapp1");
	}
	
	@Test
	public void multipleSystemAppsCanBeHosted() throws Exception
	{
		given(sysapp1).hasBeenCreated()
			.and(sysapp1Conf).supportsLocales("en", "de")
			.and(sysapp2).hasBeenCreated()
			.and(sysapp2Conf).supportsLocales("en", "de");
		when(appServer).started();
		then(appServer).requestCanBeMadeFor("/sysapp1")
			.and(appServer).requestCanBeMadeFor("/sysapp2");
	}
	
	@Test
	public void systemAppIsAutomaticallyHostedOnDeploy() throws Exception
	{
		given(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt")
			.and(appServer).started();
		when(sysapp1).populate("default")
			.and(sysapp1Conf).localesUpdatedTo("en", "de")
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
			.and(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt")
			.and(app1).hasBeenPopulated("default")
			.and(app1).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
					+ "locales: en\n"
					+ "requirePrefix: app1")
			.and(appServer).started()
			.and(appServer).appHasServlet(app1, helloWorldServlet, "/servlet/hello/*");
		then(appServer).requestForUrlReturns("/app1/servlet/hello", "Hello World!");
	}
	
	@Test
	public void otherServletsCanBeAddedWithExtensionMapping() throws Exception
	{
		given(brjs).usedForServletModel()
			.and(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt")
			.and(app1).hasBeenPopulated("default")
			.and(app1).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
					+ "locales: en\n"
					+ "requirePrefix: app1")
			.and(appServer).started()
			.and(appServer).appHasServlet(app1, helloWorldServlet, "*.mock");
		then(appServer).requestForUrlReturns("/app1/hello.mock", "Hello World!");
	}
	
	@Test
	public void newAppsAreAutomaticallyHostedWhenRunningCreateAppCommandFromADifferentModelInstance() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt")
			.and(brjs.applicationServer(appServerPort)).started();
		when(secondBrjsProcess).runCommand("create-app", "app1", "blah")
			.and(app1Conf).localesUpdatedTo("en", "de");
		then(appServer).requestCanEventuallyBeMadeFor("/app1/");
	} 
	
	@Test
	public void newAppsAreAutomaticallyHostedWhenRunningCreateAppCommandFromADifferentModelInstanceAndOnlyAppsDirectoryExists() throws Exception
	{
		given(brjs).doesNotContainFolder("apps")
			.and(brjs).containsFolder("apps")
			.and(brjs).hasBeenAuthenticallyCreated(); 
			/*and*/ secondBrjsProcess.close(); secondBrjsProcess = createNonTestModel();
			given(brjs.sdkTemplateGroup("default")).templateGroupCreated()
			.and(brjs.sdkTemplateGroup("default").template("app")).containsFile("index.html")
			.and(brjs.applicationServer(appServerPort)).started();
		when(secondBrjsProcess).runCommand("create-app", "app1", "blah");
		then(brjs.applicationServer(appServerPort)).requestCanEventuallyBeMadeFor("/app1/");
	}
	
	@Test
	public void newAppsAreAutomaticallyHostedWhenRunningCreateAppCommandFromADifferentModelInstanceAndWorkingDirIsSeperateFromSdk() throws Exception
	{
		secondaryTempFolder = org.bladerunnerjs.utility.FileUtils.createTemporaryDirectory(this.getClass());
		given(brjs).hasBeenAuthenticallyCreatedWithWorkingDir(secondaryTempFolder); 
			/*and*/ secondBrjsProcess.close(); secondBrjsProcess = createNonTestModel(secondaryTempFolder);
			given(brjs.sdkTemplateGroup("default")).templateGroupCreated()
			.and(brjs.sdkTemplateGroup("default").template("app")).containsFile("index.html")
			.and(brjs).usedForServletModel()
			.and(brjs.applicationServer(appServerPort)).started();
		when(secondBrjsProcess).runCommand("create-app", "app1", "blah");
		then(brjs.applicationServer(appServerPort)).requestCanEventuallyBeMadeFor("/app1/");
	}
	
	
	@Test
	public void newAppsAreHostedViaADifferentModelOnAppserverAfterServerRestart() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt")
			.and(brjs.applicationServer(appServerPort)).started();
		when(secondBrjsProcess).runCommand("create-app", "app1", "blah")
			.and(app1Conf).localesUpdatedTo("en", "de")
			.and(brjs.applicationServer(appServerPort)).stopped()
			.and(brjs).hasBeenAuthenticallyReCreated()
			.and(brjs.applicationServer(appServerPort)).started();
		then(appServer).requestCanEventuallyBeMadeFor("/app1/");
	}
	
	@Test
	public void exceptionIsThrownIfThereAreNoAppLibs() throws Exception {
		FileUtils.deleteDirectory(appJars.dir());
		when(brjs.applicationServer()).started();
		then(exceptions).verifyException(IllegalStateException.class, appJars.dir().getPath());
	}
	
	@Test
	public void fileWatcherThreadDoesntThrowAnExceptionWhenAFileExistsInAppsDir() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreatedWithFileWatcherThread()
			.and(templates).templateGroupCreated()
			.and(brjs).containsFile("apps/file.txt")
			.and(brjs.applicationServer(appServerPort)).started();
		when(secondBrjsProcess).runCommand("create-app", "app1", "blah")
			.and(app1Conf).localesUpdatedTo("en", "de");
		then(appServer).requestCanEventuallyBeMadeFor("/app1/");
	}
	
	@Test
	public void errorCode500IsThrownIfBadFileIsRequired() throws Exception {
		given(app1.defaultAspect()).indexPageRequires("appns/App")
			.and(app1).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
					+ "locales: en\n"
					+ "requirePrefix: app1")
			.and(app1.defaultAspect()).classFileHasContent("appns/App", "require('badFile')")
			.and(appServer).started();
		then(appServer).requestForUrlContains("/app1/v/dev/js/dev/combined/bundle.js", "Error 500");
	}
	
	@Test
	public void errorCode400IsThrownIfTheRequestIsMalformed() throws Exception {
		given(app1.defaultAspect()).indexPageHasContent("")
			.and(app1).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
					+ "locales: en\n"
					+ "requirePrefix: app1")
			.and(appServer).started();
		then(appServer).requestForUrlContains("/app1/v/dev/js/malformed-request", "Error 400");
	}
	
	@Test
	public void errorCode404IsThrownIfResourceIsNotFound() throws Exception {
		given(appServer).started();
		then(appServer).requestForUrlContains("/app1/v/dev/no-such-content-plugin", "Error 404");
	}
	
	@Test
	public void customErrorPagesCanBeConfigured() throws Exception {
		given(app1).hasBeenCreated()
			.and(app1.defaultAspect()).indexPageHasContent("")
			.and(app1Conf).supportsLocales("en")
    		.and(app1).containsFileWithContents("WEB-INF/web.xml", 
    				"<?xml version='1.0'?>\n"+
    		"<web-app xmlns='http://java.sun.com/xml/ns/javaee' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"+
    		"	xsi:schemaLocation='http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd' version='2.5'>\n"+
    		"	<servlet>\n"+
    		"		<servlet-name>BRJSDevServlet</servlet-name>\n"+
    		"		<servlet-class>org.bladerunnerjs.appserver.BRJSDevServlet</servlet-class>\n"+
    		"		<load-on-startup>1</load-on-startup>\n"+
    		"	</servlet>\n"+
    		"	<servlet-mapping>\n"+
    		"		<servlet-name>BRJSDevServlet</servlet-name>\n"+
    		"		<url-pattern>/brjs/*</url-pattern>\n"+
    		"	</servlet-mapping>\n"+
    		"	<filter>\n"+
    		"		<filter-name>BRJSDevServletFilter</filter-name>\n"+ 
    		"		<filter-class>org.bladerunnerjs.appserver.BRJSDevServletFilter</filter-class>\n"+
    		"	</filter>\n"+
    		"	<filter-mapping>\n"+
    		"		<filter-name>BRJSDevServletFilter</filter-name>\n"+ 
    		"		<url-pattern>/*</url-pattern> \n"+
    		"		<dispatcher>REQUEST</dispatcher>\n"+
    		"		<dispatcher>FORWARD</dispatcher>\n"+
    		"	</filter-mapping>\n"+
    		"	<error-page>\n"+
    		"    	<error-code>404</error-code>\n"+
    		"    	<location>/WEB-INF/error-pages/404.html</location>\n"+
    		"	</error-page>\n"+
    		"</web-app>")
			.and(app1).containsFileWithContents("WEB-INF/error-pages/404.html", "that's a 404!");
    	when(appServer).started();
		then(appServer).requestForUrlReturns("/app1/giveme404", "that's a 404!");
	}
	
}
