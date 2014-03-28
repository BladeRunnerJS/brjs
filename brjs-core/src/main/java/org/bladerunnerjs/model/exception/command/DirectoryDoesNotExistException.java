package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.plugin.CommandPlugin;

public class DirectoryDoesNotExistException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryDoesNotExistException(String path, CommandPlugin commandPlugin) {
		super("The directory '" + path + "' does not exist.", commandPlugin);
	}
}
