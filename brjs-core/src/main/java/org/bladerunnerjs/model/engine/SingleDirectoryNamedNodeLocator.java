package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SingleDirectoryNamedNodeLocator implements NamedNodeLocator
{
	private String dirName;
	private String subDirPath;
	
	public SingleDirectoryNamedNodeLocator(String dirName, String subDirPath)
	{
		this.dirName = dirName;
		this.subDirPath = subDirPath;
	}
	
	@Override
	public List<String> getLogicalNodeNames(File sourceDir)
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
	public boolean couldSupportLogicalNodeName(String logicalNodeName)
	{
		return logicalNodeName.equals(dirName);
	}
	
	@Override
	public String getDirName(String logicalNodeName)
	{
		return subDirPath;
	}
}
