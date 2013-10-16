package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.specutil.engine.NodeBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class NamedNodeBuilder extends NodeBuilder<NamedNode>
{
	public NamedNodeBuilder(SpecTest modelTest, NamedNode node) {
		super(modelTest, node);
	}
}
