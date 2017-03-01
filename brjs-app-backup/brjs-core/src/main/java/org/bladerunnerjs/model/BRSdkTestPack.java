package org.bladerunnerjs.model;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;


public class BRSdkTestPack extends TestPack
{
	
	public BRSdkTestPack(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir, name);
	}
	
	@Override
	public App app()
	{
		return root().systemApp("dummy-sdk-app");
	}
	
}
