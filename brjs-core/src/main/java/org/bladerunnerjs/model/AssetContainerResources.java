package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class AssetContainerResources {
	private final DeepAssetLocation seedResources;
	private final Map<String, ShallowAssetLocation> resources = new HashMap<>();
	private File srcLocationDir;
	private AssetContainer assetContainer;
	
	public AssetContainerResources(AssetContainer assetContainer, File srcLocationDir, File resourcesDir) {
		this.srcLocationDir = srcLocationDir;
		this.assetContainer = assetContainer;
		seedResources = new DeepAssetLocation(assetContainer, resourcesDir);
	}
	
	public AssetLocation getSeedResources() {
		return seedResources;
	}
	
	public List<AssetLocation> getResources(File srcDir) {
		List<AssetLocation> resourcesList = new ArrayList<>();	
		
		if (!srcDir.exists())
		{
			return resourcesList;
		}
		
		try {
			if(!srcLocationDir.equals(srcDir) && !FileUtils.directoryContains(srcLocationDir, srcDir)) {
				throw new RuntimeException("'" + srcDir.getAbsolutePath() + "' was not a child of '" + srcLocationDir.getAbsolutePath() + "'");
			}
			
			resourcesList.add(getSeedResources());
			resourcesList.addAll(getResourcesList(srcDir));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return resourcesList;
	}
	
	private List<AssetLocation> getResourcesList(File srcDir) {
		List<AssetLocation> resourcesList = new ArrayList<>();
		
		while (srcDir != null)
		{
			resourcesList.add(createResource(srcDir));
			if (srcDir.equals(srcLocationDir))
			{
				break;
			}
			srcDir = srcDir.getParentFile();
		}
		
		return resourcesList;
	}
	
	private AssetLocation createResource(File srcDir) {
		String srcPath = srcDir.getAbsolutePath();
		
		if(!resources.containsKey(srcPath)) {
			resources.put(srcPath, new ShallowAssetLocation(assetContainer, srcDir));
		}
		
		return resources.get(srcPath);
	}
}
