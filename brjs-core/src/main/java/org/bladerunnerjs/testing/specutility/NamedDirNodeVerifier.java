package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class NamedDirNodeVerifier extends NodeVerifier<NamedDirNode> {
	public NamedDirNodeVerifier(SpecTest modelTest, NamedDirNode node)
	{
		super(modelTest, node);
	}
}
