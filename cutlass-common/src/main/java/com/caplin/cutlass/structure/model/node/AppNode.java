package com.caplin.cutlass.structure.model.node;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.structure.model.BasicNameCalculator;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.NodeUtility;
import com.caplin.cutlass.structure.model.SuffixNameCalculator;
import com.caplin.cutlass.structure.model.path.AppPath;


public class AppNode extends Node
{
	private Map<String, Node> aspectNodes = new LinkedHashMap<String, Node>();
	private Map<String, Node> bladesetNodes = new LinkedHashMap<String, Node>();

	private File thirdpartyLibDir;
	
	private File userLibDir;
	
	public AppNode(Node parentNode, File path)
	{
		super(parentNode, path);
		
		AppPath appPath = new AppPath(path);
		thirdpartyLibDir = appPath.userLibsPath().getDir();
		userLibDir = appPath.userLibsPath().getDir();
	}
	
	@Override
	public NodeType getNodeType()
	{
		return NodeType.APP;
	}

	@Override
	public void calculateChildNodes()
	{
		getThirdpartyLibrariesNode();
		NodeUtility.updateNodes(this, nodes, NodeType.LIB, new BasicNameCalculator(), userLibDir);
		getAspectNodes();
		getBladesetNodes();
	}
	
	public AppPath getPath()
	{
		return new AppPath(getDir());
	}

	public ThirdpartyLibNode getThirdpartyLibrariesNode()
	{
		return (ThirdpartyLibNode) NodeUtility.updateNodes(this, nodes, NodeType.THIRDPARTY_LIB, new BasicNameCalculator(), thirdpartyLibDir);
	}
	
	@SuppressWarnings("unchecked")
	public List<AspectNode> getAspectNodes()
	{
		return (List<AspectNode>)(List<?>) NodeUtility.updateNodesFromDirListing(this, aspectNodes, NodeType.ASPECT, new SuffixNameCalculator(CutlassConfig.ASPECT_SUFFIX), 
				getDir(), CutlassConfig.ASPECT_SUFFIX);
	}
	
	@SuppressWarnings("unchecked")
	public List<BladesetNode> getBladesetNodes()
	{
		return (List<BladesetNode>)(List<?>) NodeUtility.updateNodesFromDirListing(this, bladesetNodes, NodeType.BLADESET, new SuffixNameCalculator(CutlassConfig.BLADESET_SUFFIX), 
				getDir(), CutlassConfig.BLADESET_SUFFIX);
	}

}
