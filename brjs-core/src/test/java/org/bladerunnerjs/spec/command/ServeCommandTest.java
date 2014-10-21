package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.plugin.plugins.commands.standard.ServeCommand.Messages.*;

import java.io.IOException;

import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.plugins.commands.standard.ServeCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;

public class ServeCommandTest extends SpecTest
{
	ApplicationServer appServer;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new ServeCommand())
			.and(brjs).hasBeenCreated()
			.and(brjs).containsFolder("apps")
			.and(brjs).containsFolder("sdk/system-applications");
		appServer = brjs.applicationServer(appServerPort);
		brjs.bladerunnerConf().setJettyPort(appServerPort);
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
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("serve", "a");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: a"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfAppServerAlreadyStarted() throws Exception
	{
		given(appServer).started();
		when(brjs).runCommand("serve");
		then(exceptions).verifyException(IOException.class, appServerPort);
	}
	
	@Test
	public void providingInvalidPortValueThrowsException() throws Exception
	{
		given(brjs).hasBeenAuthenticallyReCreated();
		when(brjs).runCommand("serve", "-p", "invalid-port");
		then(exceptions).verifyException(NumberFormatException.class)
			.whereTopLevelExceptionContainsString(CommandArgumentsException.class, INVALID_PORT_MESSAGE + " 'invalid-port'");
	}
	
	@Test
	public void serveCommandCorrectlyStartsTheAppServer() throws Exception
	{
		given(brjs).hasBeenAuthenticallyReCreated()
			.and(brjs).localeForwarderHasContents("")
			.and(brjs.app("app1")).hasBeenPopulated();
		when(brjs).runCommandInAnotherThread(10, "serve");
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
		when(brjs).runCommandInAnotherThread(10, "serve")
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
