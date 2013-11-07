package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.model.appserver.BRJSApplicationServer.Messages.*;
import static org.bladerunnerjs.core.plugin.command.standard.ServeCommand.Messages.*;

import java.io.IOException;

import org.bladerunnerjs.core.plugin.command.standard.ServeCommand;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServeCommandTest extends SpecTest
{
	ApplicationServer appServer;
	
	@Before
	public void initTestObjects() throws Exception
	{
		appServerPort = 7070;
		
		given(pluginLocator).hasCommand(new ServeCommand())
			.and(brjs).hasBeenCreated();	
		appServer = brjs.applicationServer(appServerPort);
	}
	
	@After
	public void tearDown() throws Exception
	{
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
	public void serveCommandStartsAppServer() throws Exception
	{
		given(logging).enabled();
		when(brjs).runCommand("serve");
		then(logging).infoMessageReceived(SERVER_STARTING_LOG_MSG, "BladeRunnerJS")
			.and(logging).infoMessageReceived("Application server started on port %s", "7070")
			.and(logging).infoMessageReceived("\n\t" + SERVER_STARTUP_MESSAGE + "7070/")
			.and(logging).infoMessageReceived("\t" + SERVER_STOP_INSTRUCTION_MESSAGE + "\n")
			.and(appServer).requestIsRedirected("/","/dashboard");
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated();
		when(brjs).runCommand("serve");
		then(exceptions).verifyNoOutstandingExceptions();
	}

	@Test
	public void exceptionIsThrownIfAppServerAlreadyStarted() throws Exception
	{
		given(appServer).started();
		when(brjs).runCommand("serve");
		then(exceptions).verifyException(IOException.class, "7070");
	}
}
