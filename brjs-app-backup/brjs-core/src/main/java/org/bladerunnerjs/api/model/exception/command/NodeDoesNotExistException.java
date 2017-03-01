package org.bladerunnerjs.api.model.exception.command;

import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;

/**
 * Thrown when the specified node does not exist. 
*/ 

public class NodeDoesNotExistException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public NodeDoesNotExistException(NamedNode node, CommandPlugin commandPlugin) {
		this(node.getTypeName(), node.getName(), commandPlugin);
	}
	
	public NodeDoesNotExistException(Node node, String nodeName, CommandPlugin commandPlugin) {
		this(node.getTypeName(), nodeName, commandPlugin);
	}
	
	public NodeDoesNotExistException(String type, String nodeName, CommandPlugin commandPlugin) {
		super(type + " '" + nodeName + "' does not exist", commandPlugin);
	}
}