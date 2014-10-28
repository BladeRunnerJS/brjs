package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.plugin.CommandPlugin;

/**
 * Class derived from Exception - Throwable - Object.
 * This is the superclass for 
 * @see org.bladerunnerjs.model.exception.command.ArgumentParsingException
 * @see org.bladerunnerjs.model.exception.command.DirectoryAlreadyExistsCommandException
 * @see org.bladerunnerjs.model.exception.command.DirectoryDoesNotExistCommandException
 * @see org.bladerunnerjs.model.exception.command.DirectoryNotEmptyCommandException
 * @see org.bladerunnerjs.model.exception.command.IncorrectNumberOfArgumentsException
 * @see org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException
 * @see org.bladerunnerjs.model.exception.command.NodeDoesNotExistException
*/ 

public class CommandArgumentsException extends Exception
{
	private static final long serialVersionUID = 1L;
	private final CommandPlugin commandPlugin;
	private String message;

	public CommandArgumentsException(String msg, CommandPlugin commandPlugin)
	{
		super(msg);
		this.message = msg;
		this.commandPlugin = commandPlugin;
	}

	public CommandArgumentsException(Throwable cause, CommandPlugin commandPlugin)
	{
		super(cause);
		this.message = cause.getMessage();
		this.commandPlugin = commandPlugin;
	}

	public CommandArgumentsException(String msg, Throwable cause, CommandPlugin commandPlugin)
	{
		super(msg, cause);
		this.message = msg + "; " + cause.getMessage();
		this.commandPlugin = commandPlugin;
	}
	
	@Override
	public String getMessage() {
		return message;
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
