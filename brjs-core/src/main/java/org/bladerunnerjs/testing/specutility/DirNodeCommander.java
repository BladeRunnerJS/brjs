package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.testing.specutility.engine.NodeBuilder;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class DirNodeCommander extends NodeBuilder<DirNode> {
	public DirNodeCommander(SpecTest modelTest, DirNode dirNode) {
		super(modelTest, dirNode);
	}
}
