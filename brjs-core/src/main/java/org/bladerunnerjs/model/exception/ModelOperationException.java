package org.bladerunnerjs.model.exception;

public class ModelOperationException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;
	
	public ModelOperationException(Exception e) {
		super(e);
		message = e.getMessage();
	}

	public ModelOperationException(String message) {
		super(message);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
