package com.caplin.cutlass.structure.model.node;

import java.io.File;

import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.path.WebcentricDbPath;



public class DatabaseNode extends Node
{
	
	public DatabaseNode(Node parentNode, File path)
	{
		super(parentNode, path);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.DATABASE;
	}

	@Override
	public void calculateChildNodes()
	{
		return;
	}
	
	public WebcentricDbPath getPath()
	{
		return new WebcentricDbPath(getDir());
	}
}
