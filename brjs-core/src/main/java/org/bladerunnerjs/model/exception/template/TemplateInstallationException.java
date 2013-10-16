package org.bladerunnerjs.model.exception.template;

public class TemplateInstallationException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TemplateInstallationException(Exception e) {
		super(e);
	}

	public TemplateInstallationException(String message) {
		super(message);
	}
}