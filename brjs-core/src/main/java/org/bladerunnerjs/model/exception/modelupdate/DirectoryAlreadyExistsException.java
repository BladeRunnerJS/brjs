package org.bladerunnerjs.model.exception.modelupdate;

import org.bladerunnerjs.model.engine.Node;

public class DirectoryAlreadyExistsException extends ModelUpdateException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryAlreadyExistsException(Node node) {
		super("unable to create directory at '" + node.dir().getPath() + "' as directory already exists");
	}
}