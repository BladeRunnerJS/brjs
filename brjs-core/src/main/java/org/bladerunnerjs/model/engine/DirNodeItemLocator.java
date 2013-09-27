package org.bladerunnerjs.model.engine;

import java.io.File;

public class DirNodeItemLocator implements NodeItemLocator
{
	private String subDirPath;
	
	public DirNodeItemLocator(String subDirPath)
	{
		this.subDirPath = subDirPath;
	}
	
	@Override
	public File getDir(File sourceDir)
	{
		return new File(sourceDir, subDirPath);
	}
}
