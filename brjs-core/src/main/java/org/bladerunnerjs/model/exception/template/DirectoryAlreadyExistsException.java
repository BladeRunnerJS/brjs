package org.bladerunnerjs.model.exception.template;

import org.bladerunnerjs.model.engine.Node;

public class DirectoryAlreadyExistsException extends TemplateInstallationException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryAlreadyExistsException(Node node) {
		super("unable to install template into '" + node.dir().getAbsolutePath() + "' as directory already exists");
	}
}