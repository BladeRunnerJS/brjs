package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;

public class BRSdkLib extends BRLib
{
	
	private App dummySdkApp;
	
	public BRSdkLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir, name);
		dummySdkApp = root().systemApp("dummy-sdk-app");
	}
	
	public static NodeMap<BRSdkLib> createSdkLibNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, BRSdkLib.class, "sdk/libs/javascript/br-libs", null);
	}

	@Override
	public App getApp()
	{
		return dummySdkApp;
	}
	
}
