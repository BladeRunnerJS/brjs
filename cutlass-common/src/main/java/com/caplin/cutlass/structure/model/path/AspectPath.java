package com.caplin.cutlass.structure.model.path;

import java.io.File;

import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.node.AspectNode;

public class AspectPath extends AbstractAssetsPath
{
	public static AspectPath locateAncestorPath(File path)
	{
		AspectNode aspectNode = SdkModel.getAspectNode(path);
		return (aspectNode == null) ? new AspectPath((File) null) : aspectNode.getPath();
	}
	
	public AspectPath(File path)
	{
		super(path);
	}
}
