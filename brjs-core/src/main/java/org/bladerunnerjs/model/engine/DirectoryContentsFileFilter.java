package org.bladerunnerjs.model.engine;

import java.io.File;

import org.apache.commons.io.filefilter.IOFileFilter;


public class DirectoryContentsFileFilter implements IOFileFilter
{

	private IOFileFilter childFileFilter;

	public DirectoryContentsFileFilter(IOFileFilter childFileFilter) {
		this.childFileFilter = childFileFilter;
	}

	@Override
	public boolean accept(File file)
	{
		File[] childFiles = file.listFiles();
		if (childFiles == null || childFiles.length == 0) {
			return false;
		}
		for (File childFile : childFiles) {
			if (childFileFilter.accept(childFile)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean accept(File dir, String name)
	{
		return accept(new File(dir, name));
	}
	
}
