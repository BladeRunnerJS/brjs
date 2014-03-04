package org.bladerunnerjs.model.exception;

public class NodeAlreadyRegisteredException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NodeAlreadyRegisteredException(String message) {
		super(message);
	}
}
