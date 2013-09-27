package com.caplin.cutlass.structure.model.path;

import java.io.File;

public class BladesPath extends AbstractPath
{
	public BladesPath(File path)
	{
		super(path);
	}
	
	public BladePath bladePath(String bladeName)
	{
		return new BladePath(new File(path, bladeName));
	}
}
