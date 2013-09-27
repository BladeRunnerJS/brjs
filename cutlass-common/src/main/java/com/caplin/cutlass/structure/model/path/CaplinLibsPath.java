package com.caplin.cutlass.structure.model.path;

import java.io.File;

public class CaplinLibsPath extends AbstractPath
{
	public CaplinLibsPath(File path)
	{
		super(path);
	}
	
	public ResourcesPath resourcesPath()
	{
		return new ResourcesPath(new File(path, "resources"));
	}
	
	public SrcPath srcPath()
	{
		return new SrcPath(new File(path, "src"));
	}
}
