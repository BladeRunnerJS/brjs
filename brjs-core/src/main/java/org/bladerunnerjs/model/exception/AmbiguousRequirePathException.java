package org.bladerunnerjs.model.exception;

public class AmbiguousRequirePathException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public AmbiguousRequirePathException(String message) {
		super(message);
	}
}
