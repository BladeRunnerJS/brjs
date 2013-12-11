package org.bladerunnerjs.plugin.plugins.bundlers.nodejs;

import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class NodeJsAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer) {
		return null;
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		if (assetLocation.getJsStyle().equals(NodeJsBundlerContentPlugin.JS_STYLE)) {
			return assetLocation.getAssetContainer().root().getAssetFilesWithExtension(assetLocation, NodeJsSourceModule.class, "js");
		}
		else {
			return Arrays.asList();
		}
	}
	
	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation) {
		return Arrays.asList();
	}
	
	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation) {
		return Arrays.asList();
	}
}
