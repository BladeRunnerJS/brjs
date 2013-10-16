package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class NamedDirNodeCommander extends NodeCommander<NamedDirNode> {
	public NamedDirNodeCommander(SpecTest modelTest, NamedDirNode node)
	{
		super(modelTest, node);
	}
}
