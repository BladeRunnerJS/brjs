package org.bladerunnerjs.api.model.exception.command;

import org.bladerunnerjs.api.plugin.CommandPlugin;

/**
 * Thrown when the parsing of arguments failed e.g. due to insufficient number of arguments, too many arguments, unexpected arguments or
 * missing required arguments. 
*/ 

public class ArgumentParsingException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public ArgumentParsingException(String message, CommandPlugin commandPlugin) {
		super(message, commandPlugin);
	}
}
