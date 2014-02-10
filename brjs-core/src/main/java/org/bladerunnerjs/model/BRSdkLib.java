package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;

public class BRSdkLib extends StandardJsLib
{
	
	public BRSdkLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir, name);
	}
	
	public static NodeMap<BRSdkLib> createSdkLibNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, BRSdkLib.class, "sdk/libs/javascript/br-libs", null);
	}
	
	@Override
	public String namespace()
	{
		return "br."+getName();
	}
	
}
