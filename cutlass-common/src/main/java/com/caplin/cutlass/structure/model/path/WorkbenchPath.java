package com.caplin.cutlass.structure.model.path;

import java.io.File;

public class WorkbenchPath extends AbstractPath
{
	public WorkbenchPath(File path)
	{
		super(path);
	}
	
	public WorkbenchResourcesPath resourcesPath()
	{
		return new WorkbenchResourcesPath(new File(path, "resources"));
	}
	
	public SrcPath srcPath()
	{
		return new SrcPath(new File(path, "src"));
	}
}
