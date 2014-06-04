package org.bladerunnerjs.model.engine;

import java.io.File;

import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public final class TestNode extends AbstractNode
{
	public TestNode(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
		
	}
}
