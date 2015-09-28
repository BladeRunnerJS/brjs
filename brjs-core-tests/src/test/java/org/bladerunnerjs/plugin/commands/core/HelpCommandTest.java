package org.bladerunnerjs.plugin.commands.core;

import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.plugin.commands.core.HelpCommand;
import org.bladerunnerjs.testing.utility.MockCommandPlugin;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class HelpCommandTest extends SpecTest
{
	HelpCommand helpCommand;
	MockCommandPlugin command1;
	MockCommandPlugin command2;
	MockCommandPlugin command3;
	MockCommandPlugin command4;
	
	@Before
	public void initTestObjects() throws Exception
	{
		command1 = new MockCommandPlugin("command1", "Command #1 description.", "command-usage", "Command #1 help.");
		command2 = new MockCommandPlugin("command2", "Command #2 description.", "command-usage", "Command #2 help.");
		command3 = new MockCommandPlugin("command3", "Command #3 description.", "command-usage", "Command #3 help.\ncommand #3 line 2");
		command4 = new MockCommandPlugin("command4", "Command #4 description.", "command-usage", "Command #4 help.\n badly formatted line\n  \tand a tabbed line");
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
		given(brjs).hasCommandPlugins(command1, command2, command3, command4)
			.and(brjs).hasBeenCreated();
		when(brjs).runCommand("help");
		then(logging).containsConsoleText(
			"Possible commands:",
			"  command1     : Command #1 description.                ",
			"  command2     : Command #2 description.                ",
			"  command3     : Command #3 description.                ",
			"  command4     : Command #4 description.                ",
			"  -----",
			"  help         : Prints this list of commands           ",
			"  version      : Displays the BladeRunnerJS version     ",
			"",
			"Supported flags:",
			"  --info",
			"  --debug",
			"  --pkg <log-packages> (the comma delimited list of packages to show messages from, or 'ALL' to show everything)",
			"  --show-pkg (show which class each log line comes from)");
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
		then(logging).containsConsoleText(
			"Description:",
			"  Command #1 description.",
			"",
			"Usage:",
			"  brjs command1 command-usage",
			"",
			"Help:",
			"  Command #1 help.");
	}
	
	@Test
	public void multilineHelpCommandIsCorrectlyFormatted() throws Exception
	{
		given(brjs).hasCommandPlugins(command3)
			.and(brjs).hasBeenCreated();
		when(brjs).runCommand("help", "command3");
		then(logging).containsConsoleText(
			"Description:",
			"  Command #3 description.",
			"",
			"Usage:",
			"  brjs command3 command-usage",
			"",
			"Help:",
			"  Command #3 help.",
			"  command #3 line 2");
	}
	
	@Test
	public void multilineHelpCommandIsCorrectlyFormattedWhenItContainsSomeWhitespace() throws Exception
	{
		given(brjs).hasCommandPlugins(command4)
			.and(brjs).hasBeenCreated();
		when(brjs).runCommand("help", "command4");
		then(logging).containsConsoleText(
			"Description:",
			"  Command #4 description.",
			"",
			"Usage:",
			"  brjs command4 command-usage",
			"",
			"Help:",
			"  Command #4 help.",
			"  badly formatted line",
			"  \tand a tabbed line");
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
