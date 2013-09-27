package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SingleDirNodeMapLocator implements NodeMapLocator
{
	private String dirName;
	private String subDirPath;
	
	public SingleDirNodeMapLocator(String dirName, String subDirPath)
	{
		this.dirName = dirName;
		this.subDirPath = subDirPath;
	}
	
	@Override
	public List<String> getDirs(File sourceDir)
	{
		List<String> dirs = new ArrayList<>();
		File dirFile = new File(sourceDir, subDirPath);
		
		if(dirFile.exists())
		{
			dirs.add(dirName);
		}
		
		return dirs;
	}
	
	@Override
	public boolean canHandleName(String childName)
	{
		return childName.equals(dirName);
	}
	
	@Override
	public String getDirName(String childName)
	{
		return subDirPath;
	}
}
