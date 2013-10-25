package com.caplin.cutlass.structure.model.node;

import java.io.File;

import org.bladerunnerjs.model.sinbin.CutlassConfig;
import com.caplin.cutlass.structure.model.BasicNameCalculator;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.NodeUtility;
import com.caplin.cutlass.structure.model.path.WorkbenchPath;



public class WorkbenchNode extends Node
{
	
	public WorkbenchNode(Node parentNode, File path)
	{
		super(parentNode, path);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.WORKBENCH;
	}

	@Override
	public void calculateChildNodes()
	{
		getTestNode();
	}
	
	public TestNode getTestNode()
	{
		return (TestNode) NodeUtility.updateNodes(this, nodes, NodeType.TEST, new BasicNameCalculator(), new File(getDir(), CutlassConfig.TESTS_DIR));
	}
	
	public WorkbenchPath getPath()
	{
		return new WorkbenchPath(getDir());
	}
}
