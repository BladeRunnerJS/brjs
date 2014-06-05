package org.bladerunnerjs.model.engine;

import java.io.File;

import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public final class TestItemNode extends AbstractNode
{
	public TestItemNode(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
	}
}
