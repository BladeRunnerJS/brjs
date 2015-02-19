package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.spec.engine.NodeBuilder;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.DirNode;


public class DirNodeVerifier extends NodeBuilder<DirNode> {
	public DirNodeVerifier(SpecTest modelTest, DirNode dirNode) {
		super(modelTest, dirNode);
	}
}
