package org.bladerunnerjs.model.exception.modelupdate;

import org.bladerunnerjs.model.engine.Node;

public class DirectoryAlreadyExistsModelException extends ModelUpdateException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryAlreadyExistsModelException(Node node) {
		super("Unable to create directory at '" + node.dir().getPath() + "' as directory already exists");
	}
}