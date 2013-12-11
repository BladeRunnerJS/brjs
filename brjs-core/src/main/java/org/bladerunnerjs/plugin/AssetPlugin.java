package org.bladerunnerjs.plugin;

import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;

public interface AssetPlugin extends Plugin {
	List<AssetLocation> getAssetLocations(AssetContainer assetContainer);
	List<SourceModule> getSourceModules(AssetLocation assetLocation);
	List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation);
	List<Asset> getResourceFiles(AssetLocation assetLocation);
}
