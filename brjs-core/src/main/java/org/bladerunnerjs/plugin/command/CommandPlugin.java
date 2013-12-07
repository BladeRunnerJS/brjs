package org.bladerunnerjs.plugin.command;

import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.Plugin;


public interface CommandPlugin extends Plugin
{
	public String getCommandName();
	public String getCommandDescription();
	public String getCommandUsage();
	public String getCommandHelp();
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException;
}
