package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.plugin.CommandPlugin;

public class ArgumentParsingException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public ArgumentParsingException(String message, CommandPlugin commandPlugin) {
		super(message, commandPlugin);
	}
}
