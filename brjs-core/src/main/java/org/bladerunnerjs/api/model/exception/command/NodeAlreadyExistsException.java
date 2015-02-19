package org.bladerunnerjs.api.model.exception.command;

import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.model.engine.NamedNode;

/**
 * Thrown when the specified node already exists. 
*/ 


public class NodeAlreadyExistsException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public NodeAlreadyExistsException(NamedNode node, CommandPlugin commandPlugin) {
		super(node.getTypeName() + " '" + node.getName() + "' already exists", commandPlugin);
	}
}