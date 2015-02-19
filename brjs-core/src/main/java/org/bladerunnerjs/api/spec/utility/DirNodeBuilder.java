package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.spec.engine.NodeBuilder;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.DirNode;


public class DirNodeBuilder extends NodeBuilder<DirNode> {
	public DirNodeBuilder(SpecTest modelTest, DirNode dirNode) {
		super(modelTest, dirNode);
	}
}
