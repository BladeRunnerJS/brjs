package org.bladerunnerjs.model;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class DeepResources extends ShallowResources {
	
	Map<File,Resources> resourcesMap = new LinkedHashMap<File,Resources>();
	
	public DeepResources(BRJS brjs, File dir) {
		super(brjs, dir);
	}

	@Override
	public List<LinkedAssetFile> seedResources()
	{
		List<LinkedAssetFile> assetFiles = new LinkedList<LinkedAssetFile>();
		
		if (dir.exists())
		{
    		Iterator<File> fileIterator = FileUtils.iterateFilesAndDirs(dir, FalseFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    		while (fileIterator.hasNext())
    		{
    			File dir = fileIterator.next();
    			Resources dirResources = resourcesMap.get(dir);
    			if (dirResources == null)
    			{
    				dirResources = new ShallowResources(brjs, dir);
    				resourcesMap.put(dir, dirResources);
    			}
    			assetFiles.addAll(dirResources.seedResources());		
    		}
		}
		
		return assetFiles;
	}
	
}