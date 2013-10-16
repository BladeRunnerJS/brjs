package com.caplin.cutlass.structure.model.node;

import java.io.File;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.path.UserLibsPath;

public class UserLibNode extends Node
{
	public UserLibNode(Node parentNode, File path)
	{
		super(parentNode, path);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.LIB;
	}
	
	@Override
	public void calculateChildNodes()
	{
		return;
	}
	
	public UserLibsPath getPath()
	{
		return new UserLibsPath(getDir());
	}
}
