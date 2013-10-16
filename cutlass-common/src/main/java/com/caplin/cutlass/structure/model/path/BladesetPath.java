package com.caplin.cutlass.structure.model.path;

import java.io.File;

import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.node.BladesetNode;

public class BladesetPath extends AbstractAssetsPath
{
	public static BladesetPath locateAncestorPath(File path)
	{
		BladesetNode bladesetNode = SdkModel.getBladesetNode(path);
		return (bladesetNode == null) ? new BladesetPath((File) null) : bladesetNode.getPath();
	}
	
	public BladesetPath(File path)
	{
		super(path);
	}
	
	public BladesPath bladesPath()
	{
		return new BladesPath(new File(path, "blades"));
	}
	
	// TODO: make this an abstract method which all path sub-classes have to implement
	public BladesetNode getNode()
	{
		return SdkModel.getBladesetNode(path);
	}
}
