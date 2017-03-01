package org.bladerunnerjs.api.model.exception.name;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.Node;

/**
 * Thrown when the specified Javascript package name is not valid for the node. 
*/ 

public class InvalidPackageNameException extends InvalidNameException {
	private static final long serialVersionUID = 1L;
	
	public InvalidPackageNameException(Node node, String packageName) {
		super("'" + packageName + "' within node at path '" + node.dir().getPath() + "' is not a valid javascript package name");
	}
}