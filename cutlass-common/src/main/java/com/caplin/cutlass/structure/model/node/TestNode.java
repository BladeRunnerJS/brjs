package com.caplin.cutlass.structure.model.node;

import java.io.File;

import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;



public class TestNode extends Node
{
	
	public TestNode(Node parentNode, File path)
	{
		super(parentNode, path);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.TEST;
	}

	@Override
	public void calculateChildNodes()
	{
		return;
	}

}
