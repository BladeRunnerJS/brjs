package org.bladerunnerjs.api.model.exception;

/**
 * Thrown when a relative require path goes above root.
*/

public class UnresolvableRelativeRequirePathException extends RequirePathException {
	private static final long serialVersionUID = 1L;
	private String msg;
	
	public UnresolvableRelativeRequirePathException(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String getMessage() {
		return msg;
	}
	
}
