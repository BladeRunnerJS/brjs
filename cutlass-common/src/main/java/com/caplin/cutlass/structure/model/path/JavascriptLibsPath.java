package com.caplin.cutlass.structure.model.path;

import java.io.File;

public class JavascriptLibsPath extends AbstractPath
{
	public JavascriptLibsPath(File path)
	{
		super(path);
	}
	
	public CaplinLibsPath caplinLibsPath()
	{
		return new CaplinLibsPath(new File(path, "caplin"));
	}
	
	public ThirdpartyLibsPath thirdpartyLibsPath()
	{
		return new ThirdpartyLibsPath(new File(path, "thirdparty"));
	}
}
