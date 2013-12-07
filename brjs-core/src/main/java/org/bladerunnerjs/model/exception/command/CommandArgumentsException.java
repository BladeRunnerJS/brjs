package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.plugin.command.CommandPlugin;


public class CommandArgumentsException extends Exception
{
	private static final long serialVersionUID = 1L;
	private final CommandPlugin commandPlugin;

	public CommandArgumentsException(String msg, CommandPlugin commandPlugin)
	{
		super(msg);
		this.commandPlugin = commandPlugin;
	}

	public CommandArgumentsException(Throwable cause, CommandPlugin commandPlugin)
	{
		super(cause);
		this.commandPlugin = commandPlugin;
	}

	public CommandArgumentsException(String msg, Throwable cause, CommandPlugin commandPlugin)
	{
		super(msg, cause);
		this.commandPlugin = commandPlugin;
	}
	
	@Override
	public String toString()
	{
		return getMessage() + ((getCause() != null) ? "\nCaused By: " + getCause().toString() : "");
	}
	
	public CommandPlugin getCommand() {
		return commandPlugin;
	}
}
