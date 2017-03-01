package org.bladerunnerjs.api.model.exception.template;

public class TemplateNotFoundException extends TemplateInstallationException {
	private static final long serialVersionUID = 1L;
	
	public TemplateNotFoundException(Exception e) {
		super(e);
	}

	public TemplateNotFoundException(String message) {
		super(message);
	}
	
	public TemplateNotFoundException(String message, Exception e) {
		super(message, e);
	}
	
}
