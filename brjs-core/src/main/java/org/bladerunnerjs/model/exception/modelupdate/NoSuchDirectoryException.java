package org.bladerunnerjs.model.exception.modelupdate;

import org.bladerunnerjs.model.engine.Node;

public class NoSuchDirectoryException extends ModelUpdateException {
	private static final long serialVersionUID = 1L;
	
	public NoSuchDirectoryException(Node node) {
		super("unable to delete directory at '" + node.dir().getAbsolutePath() + "' as directory does not exist");
	}
}