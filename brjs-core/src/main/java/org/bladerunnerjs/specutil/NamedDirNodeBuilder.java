package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.specutil.engine.NodeBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class NamedDirNodeBuilder extends NodeBuilder<NamedDirNode>
{
	public NamedDirNodeBuilder(SpecTest modelTest, NamedDirNode node) {
		super(modelTest, node);
	}
}
