package org.bladerunnerjs.api.model.exception.template;

import org.bladerunnerjs.model.engine.Node;

/**
 * Thrown when the template could not be installed into the specified path because the directory already exists. 
*/ 

public class TemplateDirectoryAlreadyExistsException extends TemplateInstallationException {
	private static final long serialVersionUID = 1L;
	
	public TemplateDirectoryAlreadyExistsException(Node node) {
		super("Unable to install template into '" + node.dir().getPath() + "' as directory already exists");
	}
}