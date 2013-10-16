package com.caplin.cutlass.structure.model.path;

import java.io.File;

import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.node.RootNode;

public class RootPath extends AbstractPath
{
	public static RootPath locateAncestorPath(File path)
	{
		RootNode rootNode = SdkModel.getRootNode(path);
		return (rootNode == null) ? new RootPath((File) null) : rootNode.getPath();
	}
	
	public RootPath(String path)
	{
		super(new File(path));
	}
	
	public RootPath(File path)
	{
		super(path);
	}
	
	public AppsPath appsPath()
	{
		return new AppsPath(new File(path, "apps"));
	}
	
	public SdkPath sdkPath()
	{
		return new SdkPath(new File(path, "sdk"));
	}
	
	public ConfPath confPath()
	{
		return new ConfPath(new File(path, "conf"));
	}
	
}
