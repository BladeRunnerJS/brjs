package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.plugin.commands.standard.ServeCommand.Messages.*;

import java.io.IOException;

import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.plugin.commands.standard.ServeCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
	
}
