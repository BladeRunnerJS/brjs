package com.caplin.cutlass.structure.model.node;

import java.io.File;

import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.path.ConfPath;



public class ConfigNode extends Node
{
	
	public ConfigNode(Node parentNode, File path)
	{
		super(parentNode, path);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CONFIG;
	}

	@Override
	public void calculateChildNodes()
	{
		return;
	}
	
	public ConfPath getPath()
	{
		return new ConfPath(getDir());
	}
}
