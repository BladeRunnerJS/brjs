package com.caplin.cutlass.structure.model.path;

import java.io.File;

public abstract class AbstractPath
{
	protected File path = null;
	
	public AbstractPath(File path)
	{
		this.path = path;
	}
	
	public String getPathStr()
	{
		return path.getPath();
	}
	
	public File getDir()
	{
		return path;
	}
	
	public File getDir(String subFolder)
	{
		return new File(path, subFolder);
	}
}
