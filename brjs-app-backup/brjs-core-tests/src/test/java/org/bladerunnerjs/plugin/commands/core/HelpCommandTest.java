package org.bladerunnerjs.plugin.commands.core;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.api.plugin.JSAPArgsParsingCommandPlugin;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.plugin.commands.core.HelpCommand;
import org.bladerunnerjs.testing.utility.MockCommandPlugin;
import org.junit.Before;
import org.junit.Test;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class HelpCommandTest extends SpecTest
{
	HelpCommand helpCommand;
	CommandPlugin command1;
	CommandPlugin command2;
	
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
		then(logging).containsConsoleText(
			"Possible commands:",
			"  command1     : Command #1 description.                 ",
			"  command2     : Command #2 description.                 ",
			"  -----",
			"  help         : Prints this list of commands.           ",
			"  version      : Displays the BladeRunnerJS version.     ",
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
		given(brjs).hasCommandPlugins( new MockCommandPlugin("command3", "Command #3 description.", "command-usage", "Command #3 help.\ncommand #3 line 2") )
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
		given(brjs).hasCommandPlugins( new MockCommandPlugin("command4", "Command #4 description.", "command-usage", "Command #4 help.\n extra whitespace line\n  \tand a tabbed line") )
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
			"   extra whitespace line",
			"    \tand a tabbed line");
	}
	
	@Test
	public void multilineHelpCommandIsCorrectlyFormattedWhenItContainsIncrementallyIndentedLines() throws Exception
	{
		given(brjs).hasCommandPlugins( new MockCommandPlugin("command4", "Command #4 description.", "command-usage", "line1\n  line2\n    line3") )
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
			"  line1",
			"    line2",
			"      line3");
	}
	
	@Test
	public void jsapArgsParsingCommandHelpMessageIsCorrectlyFormatted() throws Exception
	{
		given(brjs).hasCommandPlugins(new MockJSAPArgsParsingCommandPlugin())
		.and(brjs).hasBeenCreated();
		when(brjs).runCommand("help", "jsapCommand");
		then(logging).containsConsoleText(
				"Description:",
				"  mock jsap command",
				"",
				"Usage:",
				"  brjs jsapCommand [<arg1>] [<arg2>]",
				"",
				"Help:",
				"  [<arg1>]",
				"        the 1st argument",
				"  [<arg2>]",
				"        the 2nd argument");
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated();
		when(brjs).runCommand("help");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	
	private class MockJSAPArgsParsingCommandPlugin extends JSAPArgsParsingCommandPlugin {
		public void setBRJS(BRJS brjs) {}
		public String getCommandName() {
			return "jsapCommand";
		}
		public String getCommandDescription() {
			return "mock jsap command";
		}
		protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
			return 0;
		}
		protected void configureArgsParser(JSAP argsParser) throws JSAPException {
			argsParser.registerParameter(new UnflaggedOption("arg1").setHelp("the 1st argument"));
			argsParser.registerParameter(new UnflaggedOption("arg2").setHelp("the 2nd argument"));
		}
	}
	
}
