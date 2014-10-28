package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.plugin.CommandPlugin;

/**
 * Class derived from CommandArgumentException - Exception - Throwable - Object.
 * Thrown when the directory already exists on the specified path. 
*/ 

public class DirectoryAlreadyExistsCommandException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryAlreadyExistsCommandException(String path, CommandPlugin commandPlugin) {
		super("The directory '" + path + "' already exists.", commandPlugin);
	}
}