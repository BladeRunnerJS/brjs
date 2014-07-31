package com.caplin.cutlass.command.testIntegration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.model.Workbench;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;

public class IntegrationTestFinder
{
	
	private Logger logger = ThreadSafeStaticBRJSAccessor.root.logger(IntegrationTestFinder.class);
	
	public List<File> findTestDirs(BRJS brjs, File root)
	{
		return findTestContainerDirs(brjs, root, false);
	}
	
	public List<File> findTestContainerDirs(BRJS brjs, File root, boolean ignoreWorkbenches)
	{
		List<File> testDirs = new ArrayList<File>();
		
		if (!root.isDirectory())
		{
			return testDirs;
		}
		
		File[] children = FileUtility.sortFiles(root.listFiles());
		for (File child : children) 
		{
			if (child.isDirectory() && !child.isHidden() && isValidTestDir(brjs, child, ignoreWorkbenches)) 
			{
				testDirs.add(child);
			} else {
				testDirs.addAll(findTestContainerDirs(brjs, child, ignoreWorkbenches));
			}
		}
		return testDirs;
	}
	
	private boolean isValidTestDir(BRJS brjs, File dir, boolean ignoreWorkbenches)
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
		
		//boolean isWorkbenchDir = CutlassDirectoryLocator.isWorkbenchDir( dir.getParentFile().getParentFile().getParentFile() );
		boolean isWorkbenchDir = brjs.locateAncestorNodeOfClass(dir, Workbench.class) != null;
		if (ignoreWorkbenches && isWorkbenchDir)
		{
			validTestDir = false;
		}
		
		return validTestDir;
	}
	
}
