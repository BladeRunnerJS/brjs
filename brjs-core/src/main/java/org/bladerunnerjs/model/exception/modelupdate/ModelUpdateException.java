package org.bladerunnerjs.model.exception.modelupdate;

public class ModelUpdateException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;
	
	public ModelUpdateException(String message) {
		super(message);
		this.message = message;
	}

	public ModelUpdateException(Throwable e) {
		super(e);
		this.message = e.getMessage();
	}
	
	public ModelUpdateException(String message, Throwable e) {
		super(e);
		this.message = message + "; " + e.getMessage();
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}