package org.bladerunnerjs.aliasing;

public class NamespaceException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NamespaceException(String message) {
		super(message);
	}
	
	public NamespaceException(String message, Exception e) {
		super(message, e);
	}
}