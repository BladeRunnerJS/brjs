package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;


public class BRSdkTestPack extends TestPack
{
	
	public BRSdkTestPack(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir, name);
	}
	
	public static NodeMap<BRSdkTestPack> createBrSdkTestPackNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, BRSdkTestPack.class, "", null);
	}
	
	@Override
	public App getApp()
	{
		return root().systemApp("dummy-sdk-app");
	}
	
}
