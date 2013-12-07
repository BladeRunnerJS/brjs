package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.testing.specutility.engine.NodeBuilder;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class NamedDirNodeBuilder extends NodeBuilder<NamedDirNode>
{
	public NamedDirNodeBuilder(SpecTest modelTest, NamedDirNode node) {
		super(modelTest, node);
	}
}
