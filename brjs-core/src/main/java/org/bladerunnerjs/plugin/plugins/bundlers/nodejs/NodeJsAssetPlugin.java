package org.bladerunnerjs.plugin.plugins.bundlers.nodejs;

import java.io.File;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class NodeJsAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public boolean canHandleAsset(File assetFile, AssetLocation assetLocation) {
		return (assetLocation.jsStyle().equals(NodeJsContentPlugin.JS_STYLE) && assetFile.getName().endsWith(".js"));
	}
	
	@Override
	public Asset createAsset(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		return new NodeJsSourceModule(assetLocation, assetFile.getParentFile(), assetFile.getName());
	}
}
