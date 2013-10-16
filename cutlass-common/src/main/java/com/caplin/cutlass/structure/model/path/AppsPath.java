package com.caplin.cutlass.structure.model.path;

import java.io.File;

import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.node.AppsRootNode;

public class AppsPath extends AbstractPath
{
	public static AppsPath locateAncestorPath(File path)
	{
		AppsRootNode appsRootNode = SdkModel.getAppsRootNode(path);
		return (appsRootNode == null) ? new AppsPath((File) null) : appsRootNode.getPath();
	}
	
	public AppsPath(File path)
	{
		super(path);
	}
	
	public AppPath appPath(String appName)
	{
		return new AppPath(new File(path, appName));
	}
}
