package org.bladerunnerjs.plugin.commands.core;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.JSAPArgsParsingCommandPlugin;
import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.logger.ConsoleLogger;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class HelpCommand extends JSAPArgsParsingCommandPlugin
{
	private BRJS brjs;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("command").setHelp("the command you would like to view help for"));
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		this.logger = brjs.logger(this.getClass());
	}
	
	@Override
	public String getCommandName()
	{
		return "help";
	}

	@Override
	public String getCommandDescription()
	{
		return "Prints this list of commands";
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		if(parsedArgs.contains("command")) {
			getHelpForSpecificCommand(parsedArgs.getString("command"));
		}
		else {
			getHelpCommandResponse();
		}
		return 0;
	}

	private void getHelpCommandResponse()
	{
		List<CommandPlugin> coreCommands = brjs.plugins().getCoreCommandPlugins();
		List<CommandPlugin> extraCommands = brjs.plugins().getNonCoreCommandPlugins();
		
		logger.println("Possible commands:");
		for (CommandPlugin command : extraCommands)
		{
			logger.println( getHelpMessageFormatString(), command.getCommandName(), command.getCommandDescription() );
		}
		
		logger.println("  -----");
		
		for (CommandPlugin command : coreCommands)
		{
			logger.println( getHelpMessageFormatString(), command.getCommandName(), command.getCommandDescription() );
		}
		logger.println("");
		
		// TODO: should get the arg parser to display help?
		logger.println("Supported flags:");
		logger.println("  --info");
		logger.println("  --debug");
		logger.println("  --pkg <log-packages> (the comma delimited list of packages to show messages from, or '"+ConsoleLogger.LOG_ALL_PACKAGES_PACKAGE_NAME+"' to show everything)");
		logger.println("  --show-pkg (show which class each log line comes from)");
		logger.println("  --no-stats (permenantly disable anonymous tracking)");
		logger.println("  --stats (permenantly enable anonymous tracking)");
		logger.println("");
		
		logger.println("You can get detailed help for any command by typing:");
		logger.println("  brjs help <command-name>");
	}

	private void getHelpForSpecificCommand(String commandName) throws CommandArgumentsException
	{
		CommandPlugin command = brjs.plugins().commandPlugin(commandName);
		
		if(command == null) throw new CommandArgumentsException("Cannot show help, unknown command '" + commandName + "'", this);
		
		logger.println("Description:");
		logger.println("  " + command.getCommandDescription());
		logger.println("");
		
		logger.println("Usage:");
		logger.println("  brjs " + command.getCommandName() + " " + command.getCommandUsage());
		logger.println("");
		
		logger.println("Help:");
		logger.println( getFormattedHelpMessage(command) );
	}
	
	private String getFormattedHelpMessage(CommandPlugin command)
	{
		String commandHelp = command.getCommandHelp();
		StringBuilder formattedHelp = new StringBuilder();
		for (String line : StringUtils.split(commandHelp, "\n")) {
			formattedHelp.append( formatHelpMessageLine(line, 2) + "\n" );
		}
		return formattedHelp.toString();
	}
	
	private String formatHelpMessageLine(String line, int expectedWhitespaceCount) {
		int lineWhitespace = 0;
		while (lineWhitespace < line.length() && line.charAt(lineWhitespace) == ' ') {
			lineWhitespace++;
		}
		return StringUtils.repeat(' ', Math.max(0, expectedWhitespaceCount - lineWhitespace)) + line;
	}

	public String getHelpMessageFormatString()
	{		
		int commandNameSize = getLongestCommandName() + 5;
		int commandDescSize = getLongestCommandDescription() + 5;
		return "  %-"+commandNameSize+"s: %-"+commandDescSize+"s";
	}
	
	
	private int getLongestCommandName()
	{
		int longestCommandName = 0;
		for (CommandPlugin commandPlugin : brjs.plugins().commandPlugins())
		{
			longestCommandName = Math.max( longestCommandName, commandPlugin.getCommandName().length());
		}
		for (CommandPlugin commandPlugin : brjs.plugins().getCoreCommandPlugins())
		{
			longestCommandName = Math.max( longestCommandName, commandPlugin.getCommandName().length());
		}
		return longestCommandName;
	}
	
	private int getLongestCommandDescription()
	{
		int longestCommandDescription = 0;
		for (CommandPlugin commandPlugin : brjs.plugins().commandPlugins())
		{
			longestCommandDescription = Math.max( longestCommandDescription, commandPlugin.getCommandDescription().length());
		}
		for (CommandPlugin commandPlugin : brjs.plugins().getCoreCommandPlugins())
		{
			longestCommandDescription = Math.max( longestCommandDescription, commandPlugin.getCommandDescription().length());
		}
		return longestCommandDescription;
	}
}
