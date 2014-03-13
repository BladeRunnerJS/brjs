package org.bladerunnerjs.model.exception.command;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.plugin.CommandPlugin;

public class NodeDoesNotExistException extends CommandArgumentsException {
	private static final long serialVersionUID = 1L;
	
	public NodeDoesNotExistException(NamedNode node, CommandPlugin commandPlugin) {
		this(node, node.getName(), commandPlugin);
	}
	
	public NodeDoesNotExistException(Node node, String nodeName, CommandPlugin commandPlugin) {
		super(node.getClass().getSimpleName() + " '" + nodeName + "' does not exist", commandPlugin);
	}
}