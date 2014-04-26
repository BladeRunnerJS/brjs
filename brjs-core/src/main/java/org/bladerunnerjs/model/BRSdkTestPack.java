package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;


public class BRSdkTestPack extends TestPack
{
	
	public BRSdkTestPack(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir, name);
	}
	
	public static NodeList<BRSdkTestPack> createBrSdkTestPackNodeSet(Node node)
	{
		return new NodeList<>(node, BRSdkTestPack.class, "", null);
	}
	
	@Override
	public App app()
	{
		return root().systemApp("dummy-sdk-app");
	}
	
}
