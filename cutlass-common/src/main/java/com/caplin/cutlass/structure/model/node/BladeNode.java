package com.caplin.cutlass.structure.model.node;

import java.io.File;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.structure.model.BasicNameCalculator;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.NodeUtility;
import com.caplin.cutlass.structure.model.path.BladePath;


public class BladeNode extends Node
{
	private File workbenchDir;
	
	public BladeNode(Node parentNode, File path)
	{
		super(parentNode, path);
		
		BladePath bladePath = new BladePath(path);
		workbenchDir = bladePath.workbenchPath().getDir();
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BLADE;
	}

	@Override
	public void calculateChildNodes()
	{
		getTestNode();
		getWorkbenchNode();
	}
	
	public BladePath getPath()
	{
		return new BladePath(getDir());
	}
	
	public String getName()
	{
		return getDir().getName();
	}
	
	public TestNode getTestNode()
	{
		return (TestNode) NodeUtility.updateNodes(this, nodes, NodeType.TEST, new BasicNameCalculator(), new File(getDir(), CutlassConfig.TESTS_DIR));
	}
	
	public WorkbenchNode getWorkbenchNode()
	{
		return (WorkbenchNode) NodeUtility.updateNodes(this, nodes, NodeType.WORKBENCH, new BasicNameCalculator(), workbenchDir);
	}
}
