package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.core.plugin.command.CommandPlugin;

public class IncorrectNumberOfArgumentsException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public IncorrectNumberOfArgumentsException(CommandPlugin commandPlugin) {
		super("Incorrect number of arguments", commandPlugin);
	}
}