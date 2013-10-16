package org.bladerunnerjs.model.exception.name;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.Node;


public class InvalidRootPackageNameException extends InvalidNameException {
	private static final long serialVersionUID = 1L;
	
	public InvalidRootPackageNameException(String message)
	{
		super(message);
	}
	
	public InvalidRootPackageNameException(Node node, String packageName) {
		super("'" + packageName + "' within node at path '" + node.dir().getPath() + "' is not a valid root package name");
	}
}