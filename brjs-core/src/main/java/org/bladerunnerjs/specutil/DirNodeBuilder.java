package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.specutil.engine.NodeBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class DirNodeBuilder extends NodeBuilder<DirNode> {
	public DirNodeBuilder(SpecTest modelTest, DirNode dirNode) {
		super(modelTest, dirNode);
	}
}
