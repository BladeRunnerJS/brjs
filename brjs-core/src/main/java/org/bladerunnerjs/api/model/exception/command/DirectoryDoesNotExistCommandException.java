package org.bladerunnerjs.api.model.exception.command;

import org.bladerunnerjs.api.plugin.CommandPlugin;

/**
 * Thrown when the directory does not exist on the specified path. 
*/ 

public class DirectoryDoesNotExistCommandException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryDoesNotExistCommandException(String path, CommandPlugin commandPlugin) {
		super("The directory '" + path + "' does not exist.", commandPlugin);
	}
}
