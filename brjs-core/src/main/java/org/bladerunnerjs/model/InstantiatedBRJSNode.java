package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class InstantiatedBRJSNode extends AbstractBRJSNode {
	public InstantiatedBRJSNode(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	protected void registerNode() {
		// central registration of this instance has been prevented
	}
}
