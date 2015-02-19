package org.bladerunnerjs.api.model.exception.command;

/**
 * Thrown when an invalid command that does not exist has been inputted. 
*/ 

public class NoSuchCommandException extends Exception {
	private static final long serialVersionUID = 1L;
	private final String commandName;
	
	public NoSuchCommandException(String commandName) {
		super("No such command '" + commandName + "'");
		this.commandName = commandName;
	}
	
	public String getCommandName() {
		return commandName;
	}
}