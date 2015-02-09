package org.bladerunnerjs.plugin.bundlers.namespacedjs;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.base.AbstractLegacyAssetPlugin;
import org.bladerunnerjs.model.AssetFileInstantationException;

public class NamespacedJsAssetPlugin extends AbstractLegacyAssetPlugin {
	
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public boolean canHandleAsset(MemoizedFile assetFile, AssetLocation assetLocation) {
		return (assetLocation.jsStyle().equals(NamespacedJsSourceModule.JS_STYLE) && assetFile.getName().endsWith(".js"));
	}
	
	@Override
	public Asset createAsset(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		return new NamespacedJsSourceModule(assetFile, assetLocation);
	}
}
