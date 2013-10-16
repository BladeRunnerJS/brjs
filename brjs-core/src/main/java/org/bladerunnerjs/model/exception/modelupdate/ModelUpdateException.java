package org.bladerunnerjs.model.exception.modelupdate;

public class ModelUpdateException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ModelUpdateException(String message) {
		super(message);
	}

	public ModelUpdateException(Throwable e) {
		super(e);
	}
}