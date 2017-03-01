package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.spec.engine.NodeVerifier;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.NamedDirNode;


public class NamedDirNodeVerifier extends NodeVerifier<NamedDirNode> {
	public NamedDirNodeVerifier(SpecTest modelTest, NamedDirNode node)
	{
		super(modelTest, node);
	}
}
