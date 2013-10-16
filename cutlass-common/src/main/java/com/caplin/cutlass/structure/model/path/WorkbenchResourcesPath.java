package com.caplin.cutlass.structure.model.path;

import java.io.File;

public class WorkbenchResourcesPath extends ResourcesPath
{
	public WorkbenchResourcesPath(File path)
	{
		super(path);
	}
	
	public StylePath stylePath()
	{
		return new StylePath(new File(path, "style"));
	}
}
