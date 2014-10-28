package org.bladerunnerjs.model.exception.template;

/**
 * Class derived from Exception - Throwable - Object.
 * This is the superclass for 
 * @see org.bladerunnerjs.model.exception.template.TemplateDirectoryAlreadyExistsException
*/

public class TemplateInstallationException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;
	
	public TemplateInstallationException(Exception e) {
		super(e);
		this.message = e.getMessage();
	}

	public TemplateInstallationException(String message) {
		super(message);
		this.message = message;
	}
	
	public TemplateInstallationException(String message, Exception e) {
		super(message, e);
		this.message = message + "; " + e.getMessage();
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}