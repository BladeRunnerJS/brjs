package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.plugin.CommandPlugin;

public class DirectoryAlreadyExistsException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryAlreadyExistsException(String path, CommandPlugin commandPlugin) {
		super("The directory '" + path + "' already exists.", commandPlugin);
	}
}