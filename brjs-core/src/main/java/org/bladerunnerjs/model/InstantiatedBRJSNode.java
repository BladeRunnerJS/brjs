package org.bladerunnerjs.model;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

// TODO: we can probably get rid of this class once we're only invoking registerInitializedNode() on final classes, and
// provided there is no overlap between instantiated and non-instantiated nodes
public abstract class InstantiatedBRJSNode extends AbstractBRJSNode {
	public InstantiatedBRJSNode(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
	}
	
}
