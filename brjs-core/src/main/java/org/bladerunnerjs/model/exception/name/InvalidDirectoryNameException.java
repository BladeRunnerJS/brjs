package org.bladerunnerjs.model.exception.name;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.NamedNode;


public class InvalidDirectoryNameException extends InvalidNameException {
	private static final long serialVersionUID = 1L;
	
	public InvalidDirectoryNameException(NamedNode node) {
		super("'" + node.getName() + "' within node at path '" + node.dir().getPath() + "' is not a valid directory name");
	}
}