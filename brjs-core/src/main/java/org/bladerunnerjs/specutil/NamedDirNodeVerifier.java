package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class NamedDirNodeVerifier extends NodeVerifier<NamedDirNode> {
	public NamedDirNodeVerifier(SpecTest modelTest, NamedDirNode node)
	{
		super(modelTest, node);
	}
}
