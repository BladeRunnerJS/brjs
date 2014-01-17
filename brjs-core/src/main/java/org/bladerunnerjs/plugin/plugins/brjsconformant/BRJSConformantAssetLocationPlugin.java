package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.DeepAssetLocation;
import org.bladerunnerjs.model.SourceAssetLocation;
import org.bladerunnerjs.plugin.base.AbstractAssetLocationPlugin;

public class BRJSConformantAssetLocationPlugin extends AbstractAssetLocationPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public boolean canHandleAssetContainer(AssetContainer assetContainer) {
		return true;
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer, Map<String, AssetLocation> assetLocationCache) {
		if(!assetLocationCache.containsKey("resources")) {
			assetLocationCache.put("resources", new DeepAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("resources")));
			assetLocationCache.put("src", new SourceAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("src"), assetLocationCache.get("resources")));
		}
		
		List<AssetLocation> assetLocations = new ArrayList<>();
		
		assetLocations.add(assetLocationCache.get("resources"));
		assetLocations.add(assetLocationCache.get("src"));
		((SourceAssetLocation) assetLocationCache.get("src")).addChildAssetLocations(assetLocations);
		
		return assetLocations;
	}
}
