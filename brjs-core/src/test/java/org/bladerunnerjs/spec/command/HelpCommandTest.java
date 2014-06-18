package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.plugins.commands.core.HelpCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.utility.MockCommandPlugin;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class HelpCommandTest extends SpecTest
{
	HelpCommand helpCommand;
	MockCommandPlugin command1;
	MockCommandPlugin command2;
	
	@Before
	public void initTestObjects() throws Exception
	{
		command1 = new MockCommandPlugin("command1", "Command #1 description.", "command-usage", "Command #1 help.");
		command2 = new MockCommandPlugin("command2", "Command #2 description.", "command-usage", "Command #2 help.");
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception
	{
		given(brjs).hasCommandPlugins(command1)
			.and(brjs).hasBeenCreated();
		when(brjs).runCommand("help", "command1", "help");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: help"));
	}
	
	@Test
	public void helpCommandListsAllPossibleCommands() throws Exception
	{
		given(brjs).hasCommandPlugins(command1, command2)
			.and(brjs).hasBeenCreated();
		when(brjs).runCommand("help");
		then(output).containsText(
			"Possible commands:",
			"  command1     :Command #1 description.                ",
			"  command2     :Command #2 description.                ",
			"  -----",
			"  help         :Prints this list of commands           ",
			"  version      :Displays the BladeRunnerJS version     ",
			"",
			"Supported flags:",
			"  --quiet",
			"  --verbose",
			"  --debug");
	}
	
	@Test
	public void exceptionIsThrownIfTheComandDoesntExist() throws Exception
	{
		given(brjs).hasCommandPlugins(command1)
			.and(brjs).hasBeenCreated();
		when(brjs).runCommand("help", "non-existent-command");
		then(exceptions).verifyException(CommandArgumentsException.class, unquoted("Cannot show help, unknown command 'non-existent-command'"));
	}
	
	@Test
	public void helpForSpecificCommandShowsUsage() throws Exception
	{
		given(brjs).hasCommandPlugins(command1)
			.and(brjs).hasBeenCreated();
		when(brjs).runCommand("help", "command1");
		then(output).containsText(
			"Description:",
			"  Command #1 description.",
			"",
			"Usage:",
			"  brjs command1 command-usage",
			"",
			"Help:",
			"  Command #1 help.");
	}
	
	@Ignore
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated();
		when(brjs).runCommand("help");
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
