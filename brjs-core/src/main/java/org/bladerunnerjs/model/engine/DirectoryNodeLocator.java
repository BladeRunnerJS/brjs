package org.bladerunnerjs.model.engine;

import org.bladerunnerjs.api.memoization.MemoizedFile;

public class DirectoryNodeLocator implements NodeLocator
{
	private String subDirPath;
	
	public DirectoryNodeLocator(String subDirPath)
	{
		this.subDirPath = subDirPath;
	}
	
	@Override
	public MemoizedFile getNodeDir(MemoizedFile sourceDir)
	{
		return sourceDir.file(subDirPath);
	}
}
