package org.bladerunnerjs.model.exception.modelupdate;

import org.bladerunnerjs.model.engine.Node;

/**
 * Class derived from ModelUpdateException - Exception - Throwable - Object.
 * Thrown when it was attempted to delete a non-existent directory. 
*/ 

public class NoSuchDirectoryException extends ModelUpdateException {
	private static final long serialVersionUID = 1L;
	
	public NoSuchDirectoryException(Node node) {
		super("unable to delete directory at '" + node.dir().getPath() + "' as directory does not exist");
	}
}