package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;


public class DefaultBladeset extends Bladeset
{

	public DefaultBladeset(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir, "default");
	}

}
