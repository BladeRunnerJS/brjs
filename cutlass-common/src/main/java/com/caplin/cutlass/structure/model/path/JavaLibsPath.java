package com.caplin.cutlass.structure.model.path;

import java.io.File;

public class JavaLibsPath extends AbstractPath
{
	public JavaLibsPath(File path)
	{
		super(path);
	}
	
	public JavaApplicationLibsPath applicationLibsPath()
	{
		return new JavaApplicationLibsPath(new File(path, "application"));
	}
	
	public JavaSystemLibsPath systemLibsPath()
	{
		return new JavaSystemLibsPath(new File(path, "application"));
	}
}