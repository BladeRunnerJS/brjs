package org.bladerunnerjs.plugin.plugins.bundlers.html;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class HTMLAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer) {
		return new ArrayList<>();
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation, List<File> files) {
		return new ArrayList<>();
	}
	
	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation, List<File> files) {
		return assetLocation.getAssetContainer().root().getAssetFilesWithExtension(assetLocation, FullyQualifiedLinkedAsset.class, files, "html");
	}
	
	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation, List<File> files) {
		return new ArrayList<>();
	}
}
