package com.caplin.cutlass.structure.model.path;

import java.io.File;

public class ThirdpartyLibsPath extends AbstractPath
{
	public ThirdpartyLibsPath(File path)
	{
		super(path);
	}

	public ThirdpartyLibPath libPath(String library)
	{
		return new ThirdpartyLibPath(new File(path, library));
	}
}
