package org.bladerunnerjs.api.model.exception.template;

/**
 * This is the superclass for 
 * @see org.bladerunnerjs.api.model.exception.template.TemplateDirectoryAlreadyExistsException
*/

public class TemplateInstallationException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TemplateInstallationException(Exception e) {
		super(e);
	}

	public TemplateInstallationException(String message) {
		super(message);
	}
	
	public TemplateInstallationException(String message, Exception e) {
		super(message + "; " + e.getMessage(), e);
	}
}