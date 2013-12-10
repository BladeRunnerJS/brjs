package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.plugin.CommandPlugin;

public class NodeDoesNotExistException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public NodeDoesNotExistException(NamedNode node, CommandPlugin commandPlugin) {
		super(node.getClass().getSimpleName() + " '" + node.getName() + "' does not exist", commandPlugin);
	}
}