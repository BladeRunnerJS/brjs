package org.bladerunnerjs.model;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class AppJsLib extends AbstractJsLib
{
	
	public AppJsLib(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir, name);
	}
	
	public AppJsLib(RootNode rootNode, Node parent, MemoizedFile dir)
	{
		this(rootNode, parent, dir, null);
	}
	
}
