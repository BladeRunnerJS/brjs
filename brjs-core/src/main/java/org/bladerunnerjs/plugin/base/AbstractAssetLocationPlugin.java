package org.bladerunnerjs.plugin.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.plugin.AssetLocationPlugin;
import org.bladerunnerjs.plugin.AssetPlugin;


/**
 * A specialization of {@link AbstractPlugin} for developers that need to implement {@link AssetPlugin}.
 */
public abstract class AbstractAssetLocationPlugin extends AbstractPlugin implements AssetLocationPlugin {
	private Map<AssetContainer, Map<String, AssetLocation>> assetLocationCaches = new HashMap<>();
	
	public List<File> getAssetLocationDirectories(AssetContainer assetContainer) {
		Map<String, AssetLocation> assetLocationCache = getCache(assetContainer);
		List<File> assetLocationDirs = new ArrayList<>();
		
		if(canHandleAssetContainer(assetContainer)) {
			for(AssetLocation assetLocation : getAssetLocations(assetContainer, assetLocationCache)) {
				assetLocationDirs.add(assetLocation.dir());
			}
		}
		
		return assetLocationDirs;
	}
	
	public List<File> getSeedAssetLocationDirectories(AssetContainer assetContainer) {
		return new ArrayList<>();
	}
	
	public AssetLocation createAssetLocation(AssetContainer assetContainer, File dir) {
		Map<String, AssetLocation> assetLocationCache = getCache(assetContainer);
		
		for(AssetLocation assetLocation : getAssetLocations(assetContainer, assetLocationCache)) {
			if(assetLocation.dir().getAbsolutePath().equals(dir.getAbsolutePath())) {
				return assetLocation;
			}
		}
		
		return null;
	}
	
	private Map<String, AssetLocation> getCache(AssetContainer assetContainer) {
		if(!assetLocationCaches.containsKey(assetContainer)) {
			assetLocationCaches.put(assetContainer, new HashMap<>());
		}
		
		return assetLocationCaches.get(assetContainer);
	}
}
