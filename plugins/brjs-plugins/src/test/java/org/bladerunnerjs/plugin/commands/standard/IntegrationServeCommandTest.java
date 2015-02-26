package org.bladerunnerjs.plugin.commands.standard;

import static org.bladerunnerjs.appserver.BRJSApplicationServer.Messages.*;
import static org.bladerunnerjs.plugin.commands.standard.ServeCommand.Messages.*;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.plugin.commands.standard.ServeCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;

public class IntegrationServeCommandTest extends SpecTest
{
	private ApplicationServer appServer;
	private TemplateGroup templates;

	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new ServeCommand())
			.and(brjs).hasBeenCreated()
			.and(brjs).containsFolder("apps")
			.and(brjs).containsFolder("sdk/system-applications");
		appServer = brjs.applicationServer(appServerPort);
		brjs.bladerunnerConf().setJettyPort(appServerPort);
		templates = brjs.sdkTemplateGroup("default");
		brjs.appJars().create();
	}
	
	@After
	public void tearDown() throws Exception
	{
		logging.disableStoringLogs();
		logging.emptyLogStore();
		appServer = brjs.applicationServer(appServerPort);
		appServer.stop();
	}
	
	@Test
	public void serveCommandStartsAppServer() throws Exception
	{
		given(logging).enabled();
		when(brjs).runThreadedCommand("serve");
		then(logging).infoMessageReceived(SERVER_STARTING_LOG_MSG, "BladeRunnerJS")
			.and(logging).infoMessageReceived(SERVER_STARTED_LOG_MESSAGE, appServerPort)
			.and(logging).containsFormattedConsoleMessage(SERVER_STARTUP_MESSAGE + appServerPort +"/")
			.and(logging).containsFormattedConsoleMessage(SERVER_STOP_INSTRUCTION_MESSAGE + "\n")
			.and(appServer).requestIs302Redirected("/","/dashboard");
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated();
			/* and */ brjs.bladerunnerConf().setJettyPort(appServerPort);
		when(brjs).runThreadedCommand("serve");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void canOverridePortValueWithArgument() throws Exception
	{
		appServerPort = 7777;
		appServer = brjs.applicationServer(appServerPort);
		
		given(logging).enabled();
		when(brjs).runThreadedCommand("serve", "-p", "7777");
		then(logging).infoMessageReceived(SERVER_STARTING_LOG_MSG, "BladeRunnerJS")
			.and(logging).infoMessageReceived(SERVER_STARTED_LOG_MESSAGE, "7777")
			.and(logging).containsFormattedConsoleMessage(SERVER_STARTUP_MESSAGE + "7777/")
			.and(logging).containsFormattedConsoleMessage(SERVER_STOP_INSTRUCTION_MESSAGE + "\n")
			.and(appServer).requestIs302Redirected("/","/dashboard");
	}
	
	@Test
	public void serverWillServeAppsOnceStarted() throws Exception
	{
		given(brjs).hasBeenAuthenticallyReCreated()
			.and(brjs).localeForwarderHasContents("")
			.and(templates).templateGroupCreated()
			.and(templates.template("app")).containsFile("fileForApp.txt")
			.and(brjs.app("app1")).hasBeenPopulated("default");
		when(brjs).runThreadedCommand("serve");
		then(appServer).requestCanEventuallyBeMadeFor("/app1");
	}
	
	@Test
	public void serveCommandStartsTheFileWatcher() throws Exception
	{
		brjs = null;
		given(brjs).hasBeenAuthenticallyReCreated()
			.and(brjs).localeForwarderHasContents("")
			.and(brjs).usedForServletModel();
		App app = brjs.app("app1");
		Aspect aspect = app.defaultAspect();
		appServer = brjs.applicationServer();
		
		given(aspect).hasClass("appns/Class1")
			.and(aspect).hasClass("appns/Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(app).hasReceivedRequest("v/dev/js/dev/combined/bundle.js");
		when(brjs).runThreadedCommand("serve")
			.and(aspect).indexPageRefersToWithoutNotifyingFileRegistry("appns.Class2");
		then(appServer).requestCanEventuallyBeMadeWhereResponseMatches("/app1/v/dev/js/dev/combined/bundle.js", new Predicate<String>()
		{
			@Override
			public boolean apply(String input)
			{
				return input.contains("Class2 =") && !input.contains("Class1");
			}
		});
	}
	
}
