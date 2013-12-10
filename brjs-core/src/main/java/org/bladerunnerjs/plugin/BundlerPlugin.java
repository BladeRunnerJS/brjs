package org.bladerunnerjs.plugin;

import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;


public interface BundlerPlugin extends ContentPlugin {
	List<SourceModule> getSourceModules(AssetLocation assetLocation);
	List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation);
	List<Asset> getResourceFiles(AssetLocation assetLocation);
}
