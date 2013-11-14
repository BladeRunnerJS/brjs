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
	private BRJS brjs;
	
	public AssetContainerResources(BRJS brjs, File srcLocationDir, File resourcesDir) {
		this.brjs = brjs;
		this.srcLocationDir = srcLocationDir;
		seedResources = new DeepAssetLocation(brjs, resourcesDir);
	}
	
	public AssetLocation getSeedResources() {
		return seedResources;
	}
	
	public List<AssetLocation> getResources(File srcDir) {
		List<AssetLocation> resourcesList = new ArrayList<>();
		
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
		
		do {
			resourcesList.add(createResource(srcDir));
			srcDir = srcDir.getParentFile();
		} while (!srcDir.equals(srcLocationDir));
		
		return resourcesList;
	}
	
	private AssetLocation createResource(File srcDir) {
		String srcPath = srcDir.getAbsolutePath();
		
		if(!resources.containsKey(srcPath)) {
			resources.put(srcPath, new ShallowAssetLocation(brjs, srcDir));
		}
		
		return resources.get(srcPath);
	}
}
