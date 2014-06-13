package org.bladerunnerjs.model.exception.template;

import org.bladerunnerjs.model.engine.Node;

public class TemplateDirectoryAlreadyExistsException extends TemplateInstallationException {
	private static final long serialVersionUID = 1L;
	
	public TemplateDirectoryAlreadyExistsException(Node node) {
		super("Unable to install template into '" + node.dir().getPath() + "' as directory already exists");
	}
}