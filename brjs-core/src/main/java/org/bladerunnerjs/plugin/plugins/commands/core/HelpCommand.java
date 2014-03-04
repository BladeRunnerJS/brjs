package org.bladerunnerjs.plugin.plugins.commands.core;

import java.util.List;

import org.bladerunnerjs.console.ConsoleWriter;
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
	private ConsoleWriter out;
	private BRJS brjs;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("command").setHelp("the command you would like to view help for"));
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		this.out = brjs.getConsoleWriter();
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
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		if(parsedArgs.contains("command")) {
			getHelpForSpecificCommand(parsedArgs.getString("command"));
		}
		else {
			getHelpCommandResponse();
		}
	}

	private void getHelpCommandResponse()
	{
		List<CommandPlugin> coreCommands = brjs.plugins().commandList().getCoreCommands();
		List<CommandPlugin> extraCommands = brjs.plugins().commandList().getPluginCommands();
		
		out.println("Possible commands:");
		for (CommandPlugin command : extraCommands)
		{
			out.println( getHelpMessageFormatString(brjs), command.getCommandName(), command.getCommandDescription() );
		}
		
		out.println("  -----");
		
		for (CommandPlugin command : coreCommands)
		{
			out.println( getHelpMessageFormatString(brjs), command.getCommandName(), command.getCommandDescription() );
		}
		out.println();
		
		out.println("Supported flags:");
		out.println("  --quiet");
		out.println("  --verbose");
		out.println("  --debug");
	}

	private void getHelpForSpecificCommand(String commandName) throws CommandArgumentsException
	{
		CommandPlugin command = brjs.plugins().commandList().lookupTask(commandName);
		
		if(command == null) throw new CommandArgumentsException("Cannot show help, unknown command '" + commandName + "'", this);
		
		out.println("Description:");
		out.println("  " + command.getCommandDescription());
		out.println();
		
		out.println("Usage:");
		out.println("  brjs " + command.getCommandName() + " " + command.getCommandUsage());
		out.println();
		
		out.println("Help:");
		out.println("  " + command.getCommandHelp());
	}
	
	public static String getHelpMessageFormatString(BRJS brjs)
	{		
		int commandNameSize = brjs.plugins().commandList().getLongestCommandName() + 5;
		int commandDescSize = brjs.plugins().commandList().getLongestCommandDescription() + 5;
		return "  %-"+commandNameSize+"s:%-"+commandDescSize+"s";
	}
}
