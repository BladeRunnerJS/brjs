package com.caplin.cutlass.structure.model.path;

import java.io.File;

import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.node.BladeNode;

public class BladePath extends AbstractAssetsPath
{
	public static BladePath locateAncestorPath(File path)
	{
		BladeNode bladeNode = SdkModel.getBladeNode(path);
		return (bladeNode == null) ? new BladePath((File) null) : bladeNode.getPath();
	}
	
	public BladePath(File path)
	{
		super(path);
	}
	
	public WorkbenchPath workbenchPath()
	{
		return new WorkbenchPath(new File(path, "workbench"));
	}
}
