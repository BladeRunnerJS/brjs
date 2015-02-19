package org.bladerunnerjs.api.model.exception.command;

import org.bladerunnerjs.api.plugin.CommandPlugin;

/**
 * Thrown when the directory on the specified path is not empty. 
*/ 

public class DirectoryNotEmptyCommandException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryNotEmptyCommandException(String path, CommandPlugin commandPlugin) {
		super("The directory '" + path + "' is not empty.", commandPlugin);
	}
}