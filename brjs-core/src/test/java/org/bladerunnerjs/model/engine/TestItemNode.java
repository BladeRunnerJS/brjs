package org.bladerunnerjs.model.engine;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public final class TestItemNode extends AbstractNode
{
	public TestItemNode(RootNode rootNode, Node parent, MemoizedFile dir)
	{
		super(rootNode, parent, dir);
	}
}
