package com.caplin.cutlass.structure.model.path;

import java.io.File;

public class SdkLibsPath extends AbstractPath
{
	public SdkLibsPath(File path)
	{
		super(path);
	}
	
	public JavaLibsPath javaLibsPath()
	{
		return new JavaLibsPath(new File(path, "java"));
	}
	
	public JavascriptLibsPath javascriptLibsPath()
	{
		return new JavascriptLibsPath(new File(path, "javascript"));
	}
}