package com.caplin.cutlass.command.testIntegration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.model.BladeWorkbench;

public class IntegrationTestFinder
{
	
	private Logger logger = ThreadSafeStaticBRJSAccessor.root.logger(IntegrationTestFinder.class);
	
	public List<File> findTestDirs(BRJS brjs, MemoizedFile root)
	{
		return findTestContainerDirs(brjs, root, false);
	}
	
	public List<File> findTestContainerDirs(BRJS brjs, MemoizedFile root, boolean ignoreWorkbenches)
	{
		List<File> testDirs = new ArrayList<>();
		
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
	
	// This is all a huge hack - we need to move the test commands across to use the model so we dont have to guess at whether things are valid test integration dirs
	private boolean isValidTestDir(BRJS brjs, MemoizedFile dir, boolean ignoreWorkbenches)
	{		
		boolean validTestDir = dir.isDirectory() 
				&& dir.getName().equals("webdriver") 
				&& dir.getParentFile().getName().equals("test-integration");
		
		File containingDir = dir.getParentFile().getParentFile();
		File ancestorContainingDir = containingDir.getParentFile();
		if (validTestDir && !(containingDir.getName().endsWith("-aspect") || ancestorContainingDir.getName().endsWith("-aspect") || 
				containingDir.getName().equals("workbench") || ancestorContainingDir.getName().equals("workbench")))
		{
			validTestDir = false;
			logger.info("Found integration test directory in "+dir.getPath()+"\n"+
					"\tIntegration tests are only allowed in an aspect or workbench - this directory will be ignored.");
		}
		
		boolean isWorkbenchDir = brjs.locateAncestorNodeOfClass(dir, BladeWorkbench.class) != null && brjs.locateAncestorNodeOfClass(dir, BladeWorkbench.class) instanceof BladeWorkbench;

		if (ignoreWorkbenches && isWorkbenchDir)
		{
			validTestDir = false;
		}
		
		return validTestDir;
	}
	
}
