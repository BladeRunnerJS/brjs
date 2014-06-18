package com.caplin.cutlass.command.testIntegration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.logging.Logger;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;

public class IntegrationTestFinder
{
	
	private Logger logger = BRJSAccessor.root.logger(IntegrationTestFinder.class);
	
	public List<File> findTestDirs(File root)
	{
		return findTestContainerDirs(root, false);
	}
	
	public List<File> findTestContainerDirs(File root, boolean ignoreWorkbenches)
	{
		List<File> testDirs = new ArrayList<File>();
		
		if (!root.isDirectory())
		{
			return testDirs;
		}
		
		File[] children = FileUtility.sortFiles(root.listFiles());
		for (File child : children) 
		{
			if (child.isDirectory() && !child.isHidden() && isValidTestDir(child, ignoreWorkbenches)) 
			{
				testDirs.add(child);
			} else {
				testDirs.addAll(findTestContainerDirs(child, ignoreWorkbenches));
			}
		}
		return testDirs;
	}
	
	private boolean isValidTestDir(File dir, boolean ignoreWorkbenches)
	{		
		boolean validTestDir = dir.isDirectory() 
				&& dir.getName().equals(CutlassConfig.WEBDRIVER_DIRNAME) 
				&& dir.getParentFile().getName().equals(CutlassConfig.TEST_INTEGRATION_DIRNAME);
		
		File containingDir = dir.getParentFile().getParentFile().getParentFile();
		if (validTestDir && !(containingDir.getName().endsWith(CutlassConfig.ASPECT_SUFFIX) || containingDir.getName().equals("workbench")))
		{
			validTestDir = false;
			logger.info("Found integration test directory in "+dir.getPath()+"\n"+
					"\tIntegration tests are only allowed in an aspect or workbench - this directory will be ignored.");
		}
		
		boolean isWorkbenchDir = CutlassDirectoryLocator.isWorkbenchDir( dir.getParentFile().getParentFile().getParentFile() );
		if (ignoreWorkbenches && isWorkbenchDir)
		{
			validTestDir = false;
		}
		
		return validTestDir;
	}
	
}
