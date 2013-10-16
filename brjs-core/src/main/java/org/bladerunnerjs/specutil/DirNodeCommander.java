package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.specutil.engine.NodeBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class DirNodeCommander extends NodeBuilder<DirNode> {
	public DirNodeCommander(SpecTest modelTest, DirNode dirNode) {
		super(modelTest, dirNode);
	}
}
