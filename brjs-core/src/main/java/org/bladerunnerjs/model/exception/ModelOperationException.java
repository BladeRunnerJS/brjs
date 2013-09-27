package org.bladerunnerjs.model.exception;

public class ModelOperationException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ModelOperationException(Exception e) {
		super(e);
	}
}
