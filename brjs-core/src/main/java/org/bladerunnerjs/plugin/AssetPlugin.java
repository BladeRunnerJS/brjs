package org.bladerunnerjs.plugin;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.UnhandledAssetContainerException;

public interface AssetPlugin extends Plugin {
	List<AssetLocation> getAssetLocations(AssetContainer assetContainer) throws UnhandledAssetContainerException;
	List<SourceModule> getSourceModules(AssetLocation assetLocation, List<File> files);
	List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation, List<File> files);
	List<Asset> getResourceFiles(AssetLocation assetLocation, List<File> files);
}
