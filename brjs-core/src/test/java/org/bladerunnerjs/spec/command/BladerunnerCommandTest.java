package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.core.plugin.command.standard.BladerunnerCommand;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BladerunnerCommandTest extends SpecTest
{
	ApplicationServer appServer;
	
	@Before
	public void initTestObjects() throws Exception
	{
		//TODO:: make test work with a random appServerPort instead of 7070
		appServerPort = 7070;
		
		given(pluginLocator).hasCommand(new BladerunnerCommand())
			.and(brjs).hasBeenCreated();	
		appServer = brjs.applicationServer(appServerPort);
	}
	@Ignore
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("start", "a");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: a"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	@Ignore
	@Test
	public void exceptionIsThrownIfAppServerAlreadyStarted() throws Exception
	{
		given(appServer).started();
		when(brjs).runCommand("start");
//		then(exceptions).verifyException(PortAlreadyInUseException.class, unquoted("'7070'"));
	}

	@Ignore
	@Test
	public void bladerunnerCommandStartsAppServer() throws Exception
	{
		when(brjs).runCommand("start");
		then(output).containsText(
				"Bladerunner server is now running and can be accessed at http://localhost:7070/",
				"Press Ctrl + C to stop the server")
			.and(appServer).requestIsRedirected("/","/dashboard");
	}

}
