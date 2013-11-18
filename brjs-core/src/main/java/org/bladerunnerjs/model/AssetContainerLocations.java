package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class AssetContainerLocations {
	private final DeepAssetLocation seedResources;
	private final File srcLocationDir;
	private final AssetContainer assetContainer;

	final Map<File, AssetLocation> assetLocations = new HashMap<>();
	
	public AssetContainerLocations(AssetContainer assetContainer, File srcLocationDir, File resourcesDir) {
		this.srcLocationDir = srcLocationDir;
		this.assetContainer = assetContainer;
		seedResources = new DeepAssetLocation(assetContainer, resourcesDir);
	}
	
	public AssetLocation getAssetLocation(File dir) {
		AssetLocation assetLocation = assetLocations.get(dir);
		if (assetLocation == null) {
			assetLocation = new ShallowAssetLocation(assetContainer, dir);
			assetLocations.put(dir, assetLocation);
		}
		return assetLocation;
	}
	
	public List<AssetLocation> getAllAssetLocations() {
		List<AssetLocation> allAssetLocations = new ArrayList<AssetLocation>();
		allAssetLocations.add(seedResources);
		
		if (srcLocationDir.isDirectory())
		{
    		Iterator<File> fileIterator = FileUtils.iterateFilesAndDirs(srcLocationDir, FalseFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    		while (fileIterator.hasNext())
    		{
    			File dir = fileIterator.next();
    			if (!dir.equals(srcLocationDir))
    			{
    				allAssetLocations.add( getAssetLocation(dir) );
    			}
    		}
		}
		return allAssetLocations;
	}
	
	
	public AssetLocation getSeedLocation()
	{
		return seedResources;
	}
	
	
}
