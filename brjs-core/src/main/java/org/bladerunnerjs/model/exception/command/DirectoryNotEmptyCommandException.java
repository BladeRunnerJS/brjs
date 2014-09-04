package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.plugin.CommandPlugin;

public class DirectoryNotEmptyCommandException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryNotEmptyCommandException(String path, CommandPlugin commandPlugin) {
		super("The directory '" + path + "' is not empty.", commandPlugin);
	}
}