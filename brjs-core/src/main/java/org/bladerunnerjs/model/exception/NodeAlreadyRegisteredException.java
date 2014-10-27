package org.bladerunnerjs.model.exception;

/**
 * Class derived from Exception -> Throwable -> Object.
 * Thrown when a node has already been registered on the specified path. 
*/ 

public class NodeAlreadyRegisteredException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NodeAlreadyRegisteredException(String message) {
		super(message);
	}
}
