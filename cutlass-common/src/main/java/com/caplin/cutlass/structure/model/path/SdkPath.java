package com.caplin.cutlass.structure.model.path;

import java.io.File;

public class SdkPath extends AbstractPath
{
	public SdkPath(File path)
	{
		super(path);
	}
	
	public SdkDocsPath docsPath()
	{
		return new SdkDocsPath(new File(path, "docs"));
	}
	
	public SdkLibsPath libsPath()
	{
		return new SdkLibsPath(new File(path, "libs"));
	}
	
	public AppsPath systemAppsPath()
	{
		return new AppsPath(new File(path, "system-applications"));
	}

	public TemplatesPath templatesPath()
	{
		return new TemplatesPath(new File(path, "templates"));
	}
}