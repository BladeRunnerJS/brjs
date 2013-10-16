package com.caplin.cutlass.structure.model.node;

import java.io.File;
import com.caplin.cutlass.structure.model.BasicNameCalculator;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.NodeUtility;
import com.caplin.cutlass.structure.model.path.SdkPath;

public class SdkNode extends Node
{
	private File systemApps;
	private File sdkThirdpartyDir;
	
	public SdkNode(Node parentNode, File path)
	{
		super(parentNode, path);
		
		SdkPath sdkPath = new SdkPath(path);
		systemApps = sdkPath.systemAppsPath().getDir();
		sdkThirdpartyDir = sdkPath.libsPath().javascriptLibsPath().thirdpartyLibsPath().getDir();
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.SDK;
	}
	
	@Override
	public void calculateChildNodes()
	{
		getSysAppsRootNode();
		getThirdpartyLibrariesNode();
	}
	
	public SdkPath getPath()
	{
		return new SdkPath(getDir());
	}
	
	public AppsRootNode getSysAppsRootNode()
	{
		return (AppsRootNode) NodeUtility.updateNodes(this, nodes, NodeType.APPS_ROOT, new BasicNameCalculator(), systemApps);
	}
	
	public ThirdpartyLibNode getThirdpartyLibrariesNode()
	{
		return (ThirdpartyLibNode) NodeUtility.updateNodes(this, nodes, NodeType.THIRDPARTY_LIB, new BasicNameCalculator(), sdkThirdpartyDir);
	}
}
