package com.caplin.cutlass.structure.model.node;

import java.io.File;
import java.util.List;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.structure.model.BasicNameCalculator;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.NodeUtility;
import com.caplin.cutlass.structure.model.path.BladesetPath;


public class BladesetNode extends Node
{
	
	public BladesetNode(Node parentNode, File path)
	{
		super(parentNode, path);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BLADESET;
	}

	@Override
	public void calculateChildNodes()
	{
		getTestNode();
		getBladeNodes();
	}

	@SuppressWarnings("unchecked")
	public List<BladeNode> getBladeNodes()
	{
		return (List<BladeNode>)(List<?>) NodeUtility.updateNodesFromDirListing(this, nodes, NodeType.BLADE, new BasicNameCalculator(), new File(getDir(),CutlassConfig.BLADES_CONTAINER_DIR), "");
	}
	
	public BladesetPath getPath()
	{
		return new BladesetPath(getDir());
	}
	
	public String getName()
	{
		return getDir().getName().replaceFirst("-bladeset", "");
	}
	
	public TestNode getTestNode()
	{
		return (TestNode) NodeUtility.updateNodes(this, nodes, NodeType.TEST, new BasicNameCalculator(), new File(getDir(), CutlassConfig.TESTS_DIR));
	}
}
