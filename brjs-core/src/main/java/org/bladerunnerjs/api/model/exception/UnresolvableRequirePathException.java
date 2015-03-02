package org.bladerunnerjs.api.model.exception;

/**
 * Thrown when the specified request path could not be found. 
*/ 

public class UnresolvableRequirePathException extends RequirePathException {
	private static final long serialVersionUID = 1L;
	private String requirePath;
	private String sourceRequirePath;
	private Throwable cause;
	
	public UnresolvableRequirePathException(String requirePath) {
		this.requirePath = requirePath;
	}
	
	public UnresolvableRequirePathException(String requirePath, String sourceRequirePath) {
		this.requirePath = requirePath;
		this.sourceRequirePath = sourceRequirePath;
	}
	
	public UnresolvableRequirePathException(String requirePath, Exception cause) {
		this.requirePath = requirePath;
		this.cause = cause;
	}

	@Override
	public String getMessage() {
		return "Source file '" + requirePath + "' could not be found" + ((sourceRequirePath == null) ? "." : ", it was needed by '" + sourceRequirePath + "'.");
	}
	
	public void setSourceRequirePath(String sourceRequirePath) {
		this.sourceRequirePath = sourceRequirePath;
	}
	
	@Override
	public Throwable getCause() {
		return cause;
	}
}
