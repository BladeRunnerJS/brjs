package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.spec.engine.NodeBuilder;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.engine.NamedNode;


public class NamedNodeBuilder extends NodeBuilder<NamedNode>
{
	public NamedNodeBuilder(SpecTest modelTest, NamedNode node) {
		super(modelTest, node);
	}
}
