package org.bladerunnerjs.utility;

import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.events.CommandExecutedEvent;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NoSuchCommandException;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.utility.command.CommandList;


public class CommandRunner
{
	public static int run(BRJS brjs, CommandList commandList, String args[]) throws NoSuchCommandException, CommandArgumentsException, CommandOperationException
	{
		String commandName = extractCommandFromArgs(args);
		String[] commandArgs = extractCommandArgsFromArgs(args);
		CommandPlugin commandPlugin = commandList.lookupCommand(commandName);
		
		if(commandPlugin == null) throw new NoSuchCommandException(commandName);
		
		brjs.notifyObservers(new CommandExecutedEvent("cli", commandName, commandArgs), brjs);
		
		return commandPlugin.doCommand(commandArgs);
	}
	
	public static String extractCommandFromArgs(String[] args)
	{
		if (args.length > 0)
		{
			return ArrayUtils.subarray(args, 0, 1)[0];
		}
		return "";
	}
	
	public static String[] extractCommandArgsFromArgs(String[] args)
	{
		if (args.length > 0) {
			args = ArrayUtils.subarray(args, 1, args.length);
		}
		
		return args;
	}
}
