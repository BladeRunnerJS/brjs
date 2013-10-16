package com.caplin.cutlass.structure.model.node;

import java.io.File;
import java.util.List;

import com.caplin.cutlass.structure.model.BasicNameCalculator;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.NodeUtility;
import com.caplin.cutlass.structure.model.path.AppsPath;


public class AppsRootNode extends Node
{
	
	public AppsRootNode(Node parentNode, File path)
	{
		super(parentNode, path);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.APPS_ROOT;
	}
	
	@Override
	public void calculateChildNodes()
	{
		getAppNodes();
	}
	
	public AppsPath getPath()
	{
		return new AppsPath(getDir());
	}
	
	@SuppressWarnings("unchecked")
	public List<AppNode> getAppNodes()
	{
		return (List<AppNode>)(List<?>) NodeUtility.updateNodesFromDirListing(this, nodes, NodeType.APP, new BasicNameCalculator(), getDir(), "");
	}

}
