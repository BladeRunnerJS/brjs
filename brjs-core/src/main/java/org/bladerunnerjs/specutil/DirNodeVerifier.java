package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.specutil.engine.NodeBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class DirNodeVerifier extends NodeBuilder<DirNode> {
	public DirNodeVerifier(SpecTest modelTest, DirNode dirNode) {
		super(modelTest, dirNode);
	}
}
