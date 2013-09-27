package com.caplin.cutlass.bundler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BundlerTestUtils
{
	public static String[] convertToArray(List<File> files)
	{
		List<String> filePaths = new ArrayList<String>();
		for (File file : files)
		{
			filePaths.add(file.getPath().replaceAll("\\\\", "/"));
		}

		return filePaths.toArray(new String[] {});
	}

	public static List<File> convertToList(String[] inputFiles)
	{
		List<File> files = new ArrayList<File>();

		for (int i = 0; i < inputFiles.length; ++i)
		{
			files.add(new File(inputFiles[i]));
		}

		return files;
	}
}
