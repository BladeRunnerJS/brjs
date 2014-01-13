package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.DeepAssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceAssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class BRJSConformantAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer) {
		List<AssetLocation> assetLocations = new ArrayList<>();
		
		DeepAssetLocation resources = new DeepAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("resources"));
		SourceAssetLocation source = new SourceAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("src"), resources);
		
		assetLocations.add(resources);
		assetLocations.add(source);
		assetLocations.addAll(source.getChildAssetLocations());
		
		return assetLocations;
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
	
	@Override
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
}
