package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.testing.specutility.engine.NodeCommander;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class NamedDirNodeCommander extends NodeCommander<NamedDirNode> {
	public NamedDirNodeCommander(SpecTest modelTest, NamedDirNode node)
	{
		super(modelTest, node);
	}
}
