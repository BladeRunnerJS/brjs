package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.DirNode;
import org.bladerunnerjs.api.spec.engine.NodeBuilder;
import org.bladerunnerjs.api.spec.engine.SpecTest;


public class DirNodeCommander extends NodeBuilder<DirNode> {
	public DirNodeCommander(SpecTest modelTest, DirNode dirNode) {
		super(modelTest, dirNode);
	}
}
