package org.bladerunnerjs.model;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class SdkJsLib extends AbstractJsLib
{
	
	public SdkJsLib(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir);
	}
	
	public SdkJsLib(RootNode rootNode, Node parent, MemoizedFile dir)
	{
		this(rootNode, parent, dir, null);
	}
	
	@Override
	public App app()
	{
		return root().systemApp("SDK");
	}
	
}
