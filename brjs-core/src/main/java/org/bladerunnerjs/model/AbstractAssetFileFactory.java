package org.bladerunnerjs.model;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public abstract class AbstractAssetFileFactory<AF extends AssetFile> {
	
	public abstract AF createFile(AssetContainer assetContainer, File file);
	
	/**
	 * Find all files in this directory, and subdirectories, matching the fileFilter. 
	 * Uses dirFilter to check whether to recurse in to directories. If dirFilter is null it will not recurse down.
	 */
	@SuppressWarnings("unchecked")
	public List<AF> findFiles(AssetContainer assetContainer, File srcDir, IOFileFilter fileFilter, IOFileFilter dirFilter)
	{
		List<AF> files = new LinkedList<AF>();
		if (!srcDir.isDirectory()) { return files; }
		
		Collection<File> foundFiles = FileUtils.listFiles(srcDir, fileFilter, dirFilter);
		
		for (File file : foundFiles)
		{
			files.add((AF) assetContainer.root().getAssetFile(this, assetContainer, file));
		}
		
		return files;
	}
	
}
