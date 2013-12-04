package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.utility.FileUtility;


public class DirNodeMapLocator implements NodeMapLocator
{
	private String subDirPath;
	private String dirNameFilter;
	
	public DirNodeMapLocator(String subDirPath, String dirNameFilter)
	{
		this.subDirPath = subDirPath;
		this.dirNameFilter = dirNameFilter;
	}
	
	@Override
	public List<String> getDirs(File sourceDir)
	{
		List<String> dirSet = new ArrayList<>();
		File childDir = (subDirPath == null) ? sourceDir : new File(sourceDir, subDirPath);
		
		if(childDir.exists())
		{
			String dirNameMatcher = getDirNameMatcher(dirNameFilter);
			
			for(File file : FileUtility.listDirs(childDir))
			{
				if((dirNameMatcher == null) || (file.getName().matches(dirNameMatcher)))
				{
					String childName = file.getName();
					
					if(dirNameFilter != null)
					{
						childName = childName.replaceAll(dirNameFilter, "");
					}
					
					dirSet.add(childName);
				}
			}
		}
		
		return dirSet;
	}
	
	@Override
	public boolean canHandleName(String childName)
	{
		return true;
	}
	
	@Override
	public String getDirName(String childName)
	{
		if(dirNameFilter != null)
		{
			if(dirNameFilter.startsWith("^"))
			{
				childName = dirNameFilter.substring(1) + childName;
			}
			else if(dirNameFilter.endsWith("$"))
			{
				childName = childName + dirNameFilter.substring(0, dirNameFilter.length() - 1);
			}
		}
		
		return (subDirPath == null) ? childName : subDirPath + "/" + childName;
	}
	
	private String getDirNameMatcher(String dirNameFilter)
	{
		String dirNameMatcher = null;
		
		if(dirNameFilter != null)
		{
			dirNameMatcher = (dirNameFilter.startsWith("^")) ? dirNameFilter + ".*" : ".*" + dirNameFilter;
		}
		
		return dirNameMatcher;
	}
}
