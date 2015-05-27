package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.api.BRJS;


public class FileModificationRegistryRootFileFilter extends AbstractFileFilter implements IOFileFilter
{
	private List<File> rootFiles = new ArrayList<>();

	public FileModificationRegistryRootFileFilter(BRJS brjs, File brjsDir, File appsFolderPath)
	{
		rootFiles.add(brjsDir);
		if ( !appsFolderPath.getAbsolutePath().startsWith(brjsDir.getAbsolutePath()) ) {
			rootFiles.add(appsFolderPath);
		}	
	}

	@Override
	public boolean accept(File file)
	{
		return rootFiles.contains(file);
	}

}
