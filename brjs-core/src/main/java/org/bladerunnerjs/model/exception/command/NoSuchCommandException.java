package org.bladerunnerjs.model.exception.command;

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