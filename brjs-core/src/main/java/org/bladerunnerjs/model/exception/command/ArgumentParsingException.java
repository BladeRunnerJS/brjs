package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.plugin.CommandPlugin;

/**
 * Class derived from CommandArgumentsException - Exception - Throwable - Object.
 * Thrown when the parsing of arguments failed e.g. due to insufficient number of arguments, too many arguments, unexpected arguments or
 * missing required arguments. 
*/ 

public class ArgumentParsingException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public ArgumentParsingException(String message, CommandPlugin commandPlugin) {
		super(message, commandPlugin);
	}
}
