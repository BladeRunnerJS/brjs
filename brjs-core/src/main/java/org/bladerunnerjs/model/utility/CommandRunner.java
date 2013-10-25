package org.bladerunnerjs.model.utility;

import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.core.plugin.command.CommandList;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NoSuchCommandException;


public class CommandRunner
{
	public static void run(CommandList commandList, String args[]) throws NoSuchCommandException, CommandArgumentsException, CommandOperationException
	{
		String commandName = extractCommandFromArgs(args);
		String[] commandArgs = extractCommandArgsFromArgs(args);
		CommandPlugin commandPlugin = commandList.lookupTask(commandName);
		
		if(commandPlugin == null) throw new NoSuchCommandException(commandName);
		
		commandPlugin.doCommand(commandArgs);
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
