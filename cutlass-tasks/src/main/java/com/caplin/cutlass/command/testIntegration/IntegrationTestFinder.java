package com.caplin.cutlass.command.testIntegration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.model.Workbench;

import com.caplin.cutlass.CutlassConfig;

public class IntegrationTestFinder
{
	
	private Logger logger = ThreadSafeStaticBRJSAccessor.root.logger(IntegrationTestFinder.class);
	
	public List<MemoizedFile> findTestDirs(BRJS brjs, File root)
	{
		return findTestContainerDirs(brjs, brjs.getMemoizedFile(root), false);
	}
	
	public List<MemoizedFile> findTestDirs(BRJS brjs, MemoizedFile root)
	{
		return findTestContainerDirs(brjs, root, false);
	}
	
	public List<MemoizedFile> findTestContainerDirs(BRJS brjs, MemoizedFile root, boolean ignoreWorkbenches)
	{
		List<MemoizedFile> testDirs = new ArrayList<>();
		
		if (!root.isDirectory())
		{
			return testDirs;
		}
		
		MemoizedFile[] children = root.listFiles();
		for (MemoizedFile child : children) 
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
	
	private boolean isValidTestDir(BRJS brjs, MemoizedFile dir, boolean ignoreWorkbenches)
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
		
		boolean isWorkbenchDir = brjs.locateAncestorNodeOfClass(dir, Workbench.class) != null;

		if (ignoreWorkbenches && isWorkbenchDir)
		{
			validTestDir = false;
		}
		
		return validTestDir;
	}
	
}
