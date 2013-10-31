package org.bladerunnerjs.spec.command;

import java.io.IOException;

import org.bladerunnerjs.core.plugin.command.standard.ServeCommand;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ServeCommandTest extends SpecTest
{
	ApplicationServer appServer;
	
	@Before
	public void initTestObjects() throws Exception
	{
		//TODO:: make test work with a random appServerPort instead of 7070
		appServerPort = 7070;
		
		given(pluginLocator).hasCommand(new ServeCommand())
			.and(brjs).hasBeenCreated();	
		appServer = brjs.applicationServer(appServerPort);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("serve", "a");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: a"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Ignore
	@Test()
	public void exceptionIsThrownIfAppServerAlreadyStarted() throws Exception
	{
		given(appServer).started();
		when(brjs).runCommand("serve");
		then(exceptions).verifyException(IOException.class, unquoted("'7070'"));
	}

	@Ignore
	@Test
	public void serveCommandStartsAppServer() throws Exception
	{
		when(brjs).runCommand("serve");
		then(output).containsText(
				"Bladerunner server is now running and can be accessed at http://localhost:7070/",
				"Press Ctrl + C to stop the server")
			.and(appServer).requestIsRedirected("/","/dashboard");
	}
	
	@Ignore
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated();
		when(brjs).runCommand("serve");
		then(exceptions).verifyNoOutstandingExceptions();
	}

}
