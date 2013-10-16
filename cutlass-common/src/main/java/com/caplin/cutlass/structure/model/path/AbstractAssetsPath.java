package com.caplin.cutlass.structure.model.path;

import java.io.File;

public abstract class AbstractAssetsPath extends AbstractPath
{
	public AbstractAssetsPath(File path)
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
	
	public ThemesPath themesPath()
	{
		return new ThemesPath(new File(path, "themes"));
	}
}
