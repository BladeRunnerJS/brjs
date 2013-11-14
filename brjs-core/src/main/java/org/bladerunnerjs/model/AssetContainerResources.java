package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class AssetContainerResources {
	private final DeepResources seedResources;
	private final Map<String, ShallowResources> resources = new HashMap<>();
	private File srcLocationDir;
	private BRJS brjs;
	
	public AssetContainerResources(BRJS brjs, File srcLocationDir, File resourcesDir) {
		this.brjs = brjs;
		this.srcLocationDir = srcLocationDir;
		seedResources = new DeepResources(brjs, resourcesDir);
	}
	
	public Resources getSeedResources() {
		return seedResources;
	}
	
	public List<Resources> getResources(File srcDir) {
		List<Resources> resourcesList = new ArrayList<>();
		
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
	
	private List<Resources> getResourcesList(File srcDir) {
		List<Resources> resourcesList = new ArrayList<>();
		
		do {
			resourcesList.add(createResource(srcDir));
			srcDir = srcDir.getParentFile();
		} while (!srcDir.equals(srcLocationDir));
		
		return resourcesList;
	}
	
	private Resources createResource(File srcDir) {
		String srcPath = srcDir.getAbsolutePath();
		
		if(!resources.containsKey(srcPath)) {
			resources.put(srcPath, new ShallowResources(brjs, srcDir));
		}
		
		return resources.get(srcPath);
	}
}
