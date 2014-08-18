package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.FileInfo;

public class DirectoryContentsNamedNodeLocator implements NamedNodeLocator
{
	private String subDirPath;
	private String dirNameFilter;
	private RootNode rootNode;
	private String dirNameExcludeFilter;
	
	public DirectoryContentsNamedNodeLocator(RootNode rootNode, String subDirPath, String dirNameFilter, String dirNameExcludeFilter)
	{
		this.rootNode = rootNode;
		this.subDirPath = subDirPath;
		this.dirNameFilter = dirNameFilter;
		this.dirNameExcludeFilter = dirNameExcludeFilter;
	}
	
	@Override
	public List<String> getLogicalNodeNames(File sourceDir)
	{
		List<String> dirSet = new ArrayList<>();
		File childDir = (subDirPath == null) ? sourceDir : new File(sourceDir, subDirPath);
		
		if(childDir.exists())
		{
			String dirNameMatcher = getDirNameMatcher(dirNameFilter);
			String dirNameExcludeMatcher = getDirNameMatcher(dirNameExcludeFilter);
			
			for(File file : rootNode.getFileInfo(childDir).dirs())
			{
				FileInfo fileInfo = rootNode.getFileInfo(file);
				
				if ( fileInfo.isDirectory() && (dirNameMatcher == null || file.getName().matches(dirNameMatcher) ) )
				{
					String childName = file.getName();
					
					if (dirNameExcludeMatcher != null && childName.matches(dirNameExcludeMatcher)) {
						break;
					}
					
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
	public boolean couldSupportLogicalNodeName(String logicalNodeName)
	{
		return true;
	}
	
	@Override
	public String getDirName(String logicalNodeName)
	{
		String dirName = logicalNodeName;
		
		if(dirNameFilter != null)
		{
			if(dirNameFilter.startsWith("^"))
			{
				dirName = dirNameFilter.substring(1) + dirName;
			}
			else if(dirNameFilter.endsWith("$"))
			{
				dirName = dirName + dirNameFilter.substring(0, dirNameFilter.length() - 1);
			}
		}
		
		return (subDirPath == null) ? dirName : subDirPath + "/" + dirName;
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
