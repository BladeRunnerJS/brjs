package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class NamespacedJsAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		if (!(assetLocation.getAssetContainer() instanceof JsLib) && assetLocation.getJsStyle().equals(NamespacedJsBundlerContentPlugin.JS_STYLE)) {
			// TODO: blow up if the package of the assetLocation would not be a
			// valid namespace
			
			return assetLocation.getAssetContainer().root().getAssetFilesWithExtension(assetLocation, NamespacedJsSourceModule.class, "js");
		}
		else {
			return new ArrayList<>();
		}
	}
	
	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
	
	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
}
