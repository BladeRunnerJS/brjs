package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.model.engine.NamedNode;

public class NodeAlreadyExistsException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public NodeAlreadyExistsException(NamedNode node, CommandPlugin commandPlugin) {
		super(node.getClass().getSimpleName() + " '" + node.getName() + "' already exists", commandPlugin);
	}
}