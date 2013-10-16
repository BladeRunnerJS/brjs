package com.caplin.cutlass.bundler;

import java.io.File;
import java.util.List;

public class ResourceAdder
{
	public static void appendResourceDirectory(File rootDir, String subfolder, List<File> files)
	{
		File resourceDir = new File(rootDir, subfolder);
		
		if(resourceDir.exists())
		{
			files.add(resourceDir);
		}
	}
}
