package org.bladerunnerjs.api.model.exception;

/**
 * Thrown when several resources match the required path.
*/
public class AmbiguousRequirePathException extends RequirePathException {
	private static final long serialVersionUID = 1L;
	private String message;
	private String sourceRequirePath;
	
	public AmbiguousRequirePathException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return ((sourceRequirePath != null) ? "In '" + sourceRequirePath + "': " : "") + message;
	}

	public void setSourceRequirePath(String primaryRequirePath) {
		this.sourceRequirePath = primaryRequirePath;
	}
}
