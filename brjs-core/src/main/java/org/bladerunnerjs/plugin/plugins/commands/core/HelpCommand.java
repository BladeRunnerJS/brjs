package org.bladerunnerjs.plugin.plugins.commands.core;

import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class HelpCommand extends ArgsParsingCommandPlugin
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
		logger.println("  --pkg <log-packages> (the comma delimited list of packages to show messages from, or '*' to show everything)");
		logger.println("  --show-pkg (show which class each log line comes from)");
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
		logger.println("  " + command.getCommandHelp());
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
