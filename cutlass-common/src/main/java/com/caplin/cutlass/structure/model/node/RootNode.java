package com.caplin.cutlass.structure.model.node;

import java.io.File;
import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.structure.model.BasicNameCalculator;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.NodeUtility;
import com.caplin.cutlass.structure.model.path.RootPath;


public class RootNode extends Node
{
	
	public RootNode(Node parentNode, File path)
	{
		super(parentNode, path);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.ROOT;
	}
	
	@Override
	public void calculateChildNodes()
	{
		getSdkNode();
		getAppsRootNode();
		getDatabaseNode();
		getConfigNode();
		getTempNode();
		getTestResultsNode();
	}
	
	public RootPath getPath()
	{
		return new RootPath(getDir());
	}
	
	public SdkNode getSdkNode()
	{
		return (SdkNode) NodeUtility.updateNodes(this, nodes, NodeType.SDK, new BasicNameCalculator(), new File(getDir(), CutlassConfig.SDK_DIR));
	}
	
	public AppsRootNode getAppsRootNode()
	{
		return (AppsRootNode) NodeUtility.updateNodes(this, nodes, NodeType.APPS_ROOT, new BasicNameCalculator(), new File(getDir(), CutlassConfig.APPLICATIONS_DIR));
	}
	
	//TODO: remove this - the model doesnt have any concept of databases
	public DatabaseNode getDatabaseNode()
	{
		return (DatabaseNode) NodeUtility.updateNodes(this, nodes, NodeType.DATABASE, new BasicNameCalculator(), new File(getDir(), "webcentric-db"));
	}

	public ConfigNode getConfigNode()
	{
		return (ConfigNode) NodeUtility.updateNodes(this, nodes, NodeType.CONFIG, new BasicNameCalculator(), new File(getDir(), CutlassConfig.CONF_DIR));
	}
	
	public TempNode getTempNode()
	{
		return (TempNode) NodeUtility.updateNodes(this, nodes, NodeType.TEMP, new BasicNameCalculator(), new File(getDir(), CutlassConfig.TEMP_DIR));
	}
	
	public TestResultsNode getTestResultsNode()
	{
		return (TestResultsNode) NodeUtility.updateNodes(this, nodes, NodeType.TEST_RESULTS, new BasicNameCalculator(), new File(getDir(), CutlassConfig.TEST_RESULTS_DIR));
	}
	
}
