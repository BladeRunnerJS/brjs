package org.bladerunnerjs.plugin.plugins.bundlers.commonjs;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class CommonJsAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public boolean canHandleAsset(MemoizedFile assetFile, AssetLocation assetLocation) {
		return (assetLocation.jsStyle().equals(CommonJsSourceModule.JS_STYLE) && assetFile.getName().endsWith(".js"));
	}
	
	@Override
	public Asset createAsset(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		return new DefaultCommonJsSourceModule(assetFile, assetLocation);
	}
}
