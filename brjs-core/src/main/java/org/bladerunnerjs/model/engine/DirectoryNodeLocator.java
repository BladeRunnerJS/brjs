package org.bladerunnerjs.model.engine;

import java.io.File;

public class DirectoryNodeLocator implements NodeLocator
{
	private String subDirPath;
	
	public DirectoryNodeLocator(String subDirPath)
	{
		this.subDirPath = subDirPath;
	}
	
	@Override
	public File getNodeDir(File sourceDir)
	{
		return new File(sourceDir, subDirPath);
	}
}
