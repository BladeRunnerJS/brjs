package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.utility.AdhocTimer;
import org.bladerunnerjs.utility.FastDirectoryFileFilter;
import org.bladerunnerjs.utility.FileIterator;

public class DirNodeMapLocator implements NodeMapLocator
{
	private String subDirPath;
	private String dirNameFilter;
	private RootNode rootNode;
	private String dirNameMatcher;
	
	public DirNodeMapLocator(RootNode rootNode, String subDirPath, String dirNameFilter)
	{
		this.rootNode = rootNode;
		this.subDirPath = subDirPath;
		this.dirNameFilter = dirNameFilter;
		dirNameMatcher = getDirNameMatcher(dirNameFilter);
	}
	
	@Override
	public List<String> getDirs(File sourceDir)
	{
		String absPath = sourceDir.getAbsolutePath();
		if(absPath.equals("/Users/jamesturner/Code/Caplin/brjs/cutlass-sdk/workspace")){
			AdhocTimer.stack();
		}
		
		AdhocTimer.enter("getDirs\t" + absPath , false);
		AdhocTimer.enter("getDirs\t", false);
		List<String> dirSet = new ArrayList<>();
		
//		AdhocTimer.enter("getDirs:0\t", false);
		File childDir = (subDirPath == null) ? sourceDir : new File(sourceDir, subDirPath);
//		AdhocTimer.exit("getDirs:0\t", false);
		
		if(childDir.exists())
		{
//			AdhocTimer.enter("getDirs:1\t", false);
			List<File> dirs = rootNode.getFileIterator(childDir).dirs();
//			AdhocTimer.exit("getDirs:1\t", false);
			
			
			for(File file : dirs)
			{
//				AdhocTimer.enter("getDirs:2\t", false);
				boolean doit = FastDirectoryFileFilter.isDirectory(file) && (dirNameMatcher == null || file.getName().matches(dirNameMatcher) ) ;
//				AdhocTimer.exit("getDirs:2\t", false);
				
				if(doit )
				{
//					AdhocTimer.enter("getDirs:3\t", false);
					String childName = file.getName();
					
					if(dirNameFilter != null)
					{
						childName = childName.replaceAll(dirNameFilter, "");
					}
					
					dirSet.add(childName);
//					AdhocTimer.exit("getDirs:3\t", false);
				}
			}
		}
		AdhocTimer.exit("getDirs\t" + sourceDir.getAbsolutePath(), false);
		AdhocTimer.exit("getDirs\t", false);
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



