package com.caplin.cutlass.bundler.js;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileDependencies
{
	private List<File> sourceFiles;
	private List<String> thirdPartyLibraries;
	
	public FileDependencies()
	{
		sourceFiles = new ArrayList<File>();
		thirdPartyLibraries = new ArrayList<String>(Arrays.asList("caplin-bootstrap"));
	}
	
	public List<File> getSourceFiles()
	{
		return sourceFiles;
	}

	public List<String> getThirdPartyLibraries()
	{
		return thirdPartyLibraries;
	}
}
