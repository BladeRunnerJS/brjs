package com.caplin.cutlass.structure.model.path;

import java.io.File;

public class ThemesPath extends AbstractPath
{
	public ThemesPath(File path)
	{
		super(path);
	}
	
	public ThemePath themePath(String theme)
	{
		return new ThemePath(new File(path, theme));
	}
}
