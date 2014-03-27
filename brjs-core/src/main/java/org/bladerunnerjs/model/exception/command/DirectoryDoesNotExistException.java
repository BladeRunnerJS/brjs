package org.bladerunnerjs.model.exception.command;

import java.io.File;

import org.bladerunnerjs.plugin.CommandPlugin;

public class DirectoryDoesNotExistException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryDoesNotExistException(File directory, CommandPlugin commandPlugin) {
		super("The directory '" + directory + "' does not exist.", commandPlugin);
	}
}
