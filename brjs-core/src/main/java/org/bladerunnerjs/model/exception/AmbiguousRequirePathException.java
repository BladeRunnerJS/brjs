package org.bladerunnerjs.model.exception;


public class AmbiguousRequirePathException extends RequirePathException {
	private static final long serialVersionUID = 1L;
	private String message;
	
	public AmbiguousRequirePathException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	public void setSourceRequirePath(String primaryRequirePath) {
		// TODO Auto-generated method stub
		
	}
}
