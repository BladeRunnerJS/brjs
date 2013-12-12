package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

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
		DeepAssetLocation resources = new DeepAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("resources"));
		SourceAssetLocation source = new SourceAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("src"), resources);
		List<AssetLocation> assetLocations = new ArrayList<>();
		
		assetLocations.add(resources);
		assetLocations.add(source);
		assetLocations.addAll(source.getChildAssetLocations());
		
		return assetLocations;
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation, List<File> files) {
		return new ArrayList<>();
	}
	
	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation, List<File> files) {
		return new ArrayList<>();
	}
	
	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation, List<File> files) {
		return new ArrayList<>();
	}
}
