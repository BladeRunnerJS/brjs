package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.spec.engine.NodeCommander;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.NamedDirNode;


public class NamedDirNodeCommander extends NodeCommander<NamedDirNode> {
	public NamedDirNodeCommander(SpecTest modelTest, NamedDirNode node)
	{
		super(modelTest, node);
	}
}
