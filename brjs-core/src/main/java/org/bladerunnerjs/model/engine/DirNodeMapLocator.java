package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirNodeMapLocator implements NodeMapLocator
{
	private String subDirPath;
	private String dirNameFilter;
	private RootNode rootNode;
	
	public DirNodeMapLocator(RootNode rootNode, String subDirPath, String dirNameFilter)
	{
		this.rootNode = rootNode;
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
			
			for(File file : rootNode.getFileInfo(childDir).dirs())
			{
				if( file.isDirectory() && (dirNameMatcher == null || file.getName().matches(dirNameMatcher) ) )
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
