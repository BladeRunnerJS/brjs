package com.caplin.cutlass.structure.model.node;

import java.io.File;

import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.path.ThirdpartyLibsPath;



public class ThirdpartyLibNode extends Node
{
	
	public ThirdpartyLibNode(Node parentNode, File path)
	{
		super(parentNode, path);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.THIRDPARTY_LIB;
	}

	@Override
	public void calculateChildNodes()
	{
		return;
	}
	
	public ThirdpartyLibsPath getPath()
	{
		return new ThirdpartyLibsPath(getDir());
	}
}
