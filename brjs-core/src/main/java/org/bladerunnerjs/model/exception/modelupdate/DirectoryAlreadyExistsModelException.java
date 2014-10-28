package org.bladerunnerjs.model.exception.modelupdate;

import org.bladerunnerjs.model.engine.Node;

/**
 * Class derived from ModelUpdateException - Exception - Throwable - Object.
 * Thrown when it was attempted to create a directory that already exists on the specified path. 
*/ 

public class DirectoryAlreadyExistsModelException extends ModelUpdateException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryAlreadyExistsModelException(Node node) {
		super("Unable to create directory at '" + node.dir().getPath() + "' as directory already exists");
	}
}