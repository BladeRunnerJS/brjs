package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.testing.specutility.engine.NodeBuilder;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class NamedNodeBuilder extends NodeBuilder<NamedNode>
{
	public NamedNodeBuilder(SpecTest modelTest, NamedNode node) {
		super(modelTest, node);
	}
}
