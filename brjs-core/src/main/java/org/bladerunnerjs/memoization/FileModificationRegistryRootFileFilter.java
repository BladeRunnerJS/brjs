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
		String fileAbsolutePath = file.getAbsolutePath(); // check the absolute path and if the objects are equal in case one is a File and the other a MemoizedFile 
		for (File rootFile : rootFiles) {
			if (rootFile == file || rootFile.getAbsolutePath().equals(fileAbsolutePath)) {
				return true;
			}
		}
		return false;
	}

}
